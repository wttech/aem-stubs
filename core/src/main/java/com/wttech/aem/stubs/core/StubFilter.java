package com.wttech.aem.stubs.core;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.*;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = Filter.class,
        property = {
            HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=("
                    + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=*)"
        },
        configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = StubFilter.Config.class)
public class StubFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(StubFilter.class);

    private static final String INTERNAL_DIR = "internal";

    private static final String FAIL_PATH = INTERNAL_DIR + "/fail.groovy";

    private static final String MISSING_PATH = INTERNAL_DIR + "/missing.groovy";

    private static final List<String> SPECIAL_PATHS = List.of(FAIL_PATH, MISSING_PATH);

    @Reference
    private StubRepository repository;

    private Config config;

    @ObjectClassDefinition(name = "AEM Stubs - HTTP Filter")
    public @interface Config {

        @AttributeDefinition(name = "Enabled")
        boolean enabled() default true;

        @AttributeDefinition(name = "Whiteboard Filter Regex")
        String[] osgi_http_whiteboard_filter_regex();
    }

    @Activate
    @Modified
    protected void activate(Config config) {
        this.config = config;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (!config.enabled()) {
            chain.doFilter(req, res);
            return;
        }

        var request = (HttpServletRequest) req;
        var response = (HttpServletResponse) res;

        try (var resolver = repository.createResolver()) {
            try {
                var it = repository.findStubs(resolver).iterator();
                while (it.hasNext()) {
                    var stub = it.next();
                    if (!isSpecial(stub) && stub.request(request)) {
                        stub.respond(request, response);
                        return;
                    }
                }
            } catch (StubException e) {
                LOG.error("Stubs error!", e);

                var stub = findSpecial(resolver, FAIL_PATH).orElse(null);
                if (stub != null) {
                    try {
                        stub.fail(request, response, e);
                    } catch (StubException e2) {
                        LOG.error("Stubs fail error!", e2);
                        response.sendError(
                                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Stubs fail error. " + e.getMessage());
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Stubs error. " + e.getMessage());
                }
                return;
            }

            var stub = findSpecial(resolver, MISSING_PATH).orElse(null);
            if (stub != null) {
                try {
                    stub.respond(request, response);
                } catch (StubException e) {
                    LOG.error("Stubs missing error!", e);
                    response.sendError(
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Stubs missing error. " + e.getMessage());
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Stub not found!");
            }
            return;
        } catch (LoginException e) {
            LOG.error("Stubs repository error!", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Stubs error. " + e.getMessage());
        }
    }

    private Optional<Stub> findSpecial(ResourceResolver resolver, String subPath) {
        return repository.findSpecialStub(resolver, subPath);
    }

    private boolean isSpecial(Stub stub) {
        return SPECIAL_PATHS.stream().anyMatch(n -> StringUtils.endsWith(stub.getId(), "/" + n));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
