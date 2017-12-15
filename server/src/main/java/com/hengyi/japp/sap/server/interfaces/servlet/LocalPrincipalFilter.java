package com.hengyi.japp.sap.server.interfaces.servlet;

import com.hengyi.japp.sap.server.domain.Operator;
import com.hengyi.japp.sap.server.domain.repository.OperatorRepository;
import com.sun.security.auth.UserPrincipal;
import org.jzb.exception.JNonAuthenticationError;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.security.Principal;

/**
 * Created by jzb on 17-7-11.
 */
public class LocalPrincipalFilter implements Filter {
    @Inject
    private OperatorRepository operatorRepository;

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) req) {
            @Override
            public Principal getUserPrincipal() {
                try {
                    String authorization = getHeader("Authorization");
                    String jwtToken = authorization.substring(7);
                    Operator operator = operatorRepository.findByJwt(jwtToken);
                    return new UserPrincipal(operator.getId());
                } catch (Exception e) {
                    throw new JNonAuthenticationError();
                }
            }
        }, resp);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }

}
