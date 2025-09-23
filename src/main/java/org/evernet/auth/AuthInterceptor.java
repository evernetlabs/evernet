package org.evernet.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.evernet.auth.authenticator.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final Map<Class<?>, Authenticator> authenticatorMap;

    @Autowired
    public AuthInterceptor(List<Authenticator> authenticators) {
        this.authenticatorMap = new HashMap<>();
        authenticators.forEach(authenticator -> this.authenticatorMap.put(authenticator.getType(), authenticator));
    }

    public boolean preHandle(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) {
        if (handler instanceof HandlerMethod) {
            Object handlerBean = ((HandlerMethod) handler).getBean();

            Class<?> superClass = handlerBean.getClass().getSuperclass();
            if (Objects.nonNull(superClass)) {
                Authenticator authenticator = authenticatorMap.getOrDefault(superClass, null);

                if (Objects.nonNull(authenticator)) {
                    authenticator.authenticate(request);
                }
            }
        }

        return true;
    }
}