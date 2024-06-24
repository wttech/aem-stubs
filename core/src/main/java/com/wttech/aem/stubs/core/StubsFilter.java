package com.wttech.aem.stubs.core.filters;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

@Component(service = Filter.class)
@HttpWhiteboardFilterPattern("/stubs/*")
public class StubsFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(StubsFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code here
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Pre-processing code here

        LOG.info("Stubs - Hello world!");

        chain.doFilter(request, response);

        // Post-processing code here
    }

    @Override
    public void destroy() {
        // Cleanup code here
    }
}