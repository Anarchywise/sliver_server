package com.zh.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.util.Map;


public class JsonUsernamePasswordFilter extends UsernamePasswordAuthenticationFilter {

    @SuppressWarnings("deprecation")
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //仅支持POST方法
        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("当前认证不支持：" + request.getMethod());
        }
        if (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE) || request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> userInfo = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                String userename = userInfo.get(getUsernameParameter());
                String password = userInfo.get(getPasswordParameter());
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userename, password);
                setDetails(request, token);
                return this.getAuthenticationManager().authenticate(token);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.attemptAuthentication(request, response);
    }

}