package com.day.examp3.filter;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserInterceptor  implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String user_id = (String) request.getSession().getAttribute("user_id");
        if(user_id==null){
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
