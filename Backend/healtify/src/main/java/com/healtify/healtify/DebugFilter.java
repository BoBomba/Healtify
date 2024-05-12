package com.healtify.healtify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DebugFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(DebugFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        LOG.info("Request Headers start");
        Collection<String> requestHeaders = (Collection<String>) request.getHeaderNames();
        for (String header : requestHeaders) {
            LOG.info(header + " " + request.getHeader(header));
        }
        LOG.info("Request Headers end");

        chain.doFilter(req, res);

        LOG.info("Response Headers start");
        Collection<String> responseHeaders = response.getHeaderNames();
        for (String header : responseHeaders) {
            LOG.info(header + " " + response.getHeader(header));
        }
        LOG.info("Response Headers end");
    }
}