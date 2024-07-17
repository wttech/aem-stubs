package com.wttech.aem.stubs.core;

import org.apache.sling.api.resource.LoginException;
import org.osgi.service.component.annotations.*;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(
        service = Filter.class,
        property = {
                HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=*)"
        },
        configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(ocd = StubFilter.Config.class)
public class StubFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(StubFilter.class);

    @Reference
    private StubRepository repository;

    private Config config;

    @ObjectClassDefinition(name = "AEM Stubs - HTTP Filter")
    public @interface Config {

        @AttributeDefinition(name = "Enabled")
        boolean enabled();

        @AttributeDefinition(name = "Whiteboard Filter Regex")
        String[] osgi_http_whiteboard_filter_regex();
    }

    @Activate
    @Modified
    protected void activate(Config config) {
        this.config = config;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException {
        var request = (HttpServletRequest) req;
        var response = (HttpServletResponse) res;

        try (var resolver = repository.createResolver()) {
            var it = repository.findStubs(resolver).iterator();
            while (it.hasNext()) {
                var stub = it.next();
                try {
                    if (stub.request(request)) {
                        stub.respond(request, response);

                        return;
                    }
                } catch (StubRequestException e) {
                    LOG.error(String.format("Cannot request stub '%s'!", stub.getId()), e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot request stub: " + e.getMessage());
                    return;
                } catch (StubResponseException e) {
                    LOG.error(String.format("Cannot respond with stub '%s'!", stub.getId()), e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot respond with stub: " + e.getMessage());
                    return;
                }
            }
        } catch (StubException | LoginException e) {
            LOG.error("Stubs error!", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Stubs error: " + e.getMessage());
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Cannot find any stub for current request!");
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
