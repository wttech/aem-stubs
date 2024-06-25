package com.wttech.aem.stubs.core;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
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
@Designate(ocd = StubsFilter.Config.class)
public class StubsFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(StubsFilter.class);

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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;


        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write("{\"message\": \"Hello World!\"}");


       // LOG.info("Stubs | Hello World!");

       // chain.doFilter(request, response);
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