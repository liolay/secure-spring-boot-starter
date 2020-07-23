package com.howuc.framework.safe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.howuc.framework.safe.filter.SecureHandlerMethodInterceptorAdapter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RefreshScope
@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties({SafeProperties.class})
public class SessionAutoConfiguration implements WebMvcConfigurer {
    private final SafeProperties safeProperties;

    public SessionAutoConfiguration(SafeProperties safeProperties,
                                    StringRedisTemplate redisTemplate,
                                    ObjectProvider<AuthorizingService> authorizingServiceProvider,
                                    ObjectMapper objectMapper) {
        SessionManager.redisTemplate = redisTemplate;
        SessionManager.safeProperties = safeProperties;
        SessionManager.authorizingServiceProvider = authorizingServiceProvider;
        SessionManager.objectMapper = objectMapper;
        AES.safeProperties = safeProperties;
        AccessToken.safeProperties = safeProperties;
        AccessToken.objectMapper = objectMapper;
        this.safeProperties = safeProperties;
    }

    @Bean
    public static FilterRegistrationBean<SafeFilter> safeFilterRegistration(SafeFilter safeFilter) {
        FilterRegistrationBean<SafeFilter> registration = new FilterRegistrationBean<>(safeFilter);
        registration.setOrder(SafeFilter.DEFAULT_ORDER);
        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecureHandlerMethodInterceptorAdapter(safeProperties.isHideNotExposedHandler())).addPathPatterns("/*");
    }

    @Bean
    @ConditionalOnMissingBean(AuthorizingService.class)
    public DefaultAuthorizingService defaultAuthorizingService() {
        return new DefaultAuthorizingService();
    }

    @Bean
    @ConditionalOnMissingBean(SafeFilter.class)
    public SafeFilter safeFilter(SafeProperties safeProperties, ObjectMapper objectMapper) {
        return new SafeFilter(safeProperties, objectMapper);
    }
}
