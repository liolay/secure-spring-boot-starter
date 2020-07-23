package com.howuc.framework.secure;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class SafeFilter extends OncePerRequestFilter {
    public static final int DEFAULT_ORDER = Integer.MIN_VALUE + 50;
    private SafeProperties safeProperties;
    private ObjectMapper objectMapper;

    public SafeFilter(SafeProperties safeProperties, ObjectMapper objectMapper) {
        this.safeProperties = safeProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        WebContext.set(new WebContext.Context(request, response));

        String encryptToken = request.getHeader(safeProperties.getTokenSymbol());
        try {
            if (StringUtils.isNotBlank(encryptToken)) {
                objectMapper.readValue(AES.decrypt(encryptToken), AccessToken.class).touch().store();
            }
        } catch (Exception e) {
            log.error("decrypt token error", e);
        }

        if (WebContext.get().getAccessToken() == null) AccessToken.create().store();

        WebContext.get().getAccessToken().write();

        try {
            filterChain.doFilter(request, response);
        } finally {
            WebContext.reset();
        }
    }
}
