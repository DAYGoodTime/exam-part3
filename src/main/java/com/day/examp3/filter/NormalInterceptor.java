package com.day.examp3.filter;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NormalInterceptor  implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String user_id = (String) request.getSession().getAttribute("user_id");
        if(user_id==null){
            Cookie[] cookies = request.getCookies();
            if(cookies!=null&&cookies.length!=0) {
                for (Cookie cookie:cookies) {
                    switch (cookie.getName()){
                        case "user_id":request.getSession().setAttribute("user_id",cookie.getValue());break;
                        case "username":request.getSession().setAttribute("username",cookie.getValue());break;
                        case "user_img":request.getSession().setAttribute("user_img",cookie.getValue());break;
                        case "isAdmin":request.getSession().setAttribute("isAdmin",cookie.getValue());break;
                        case "userShoppings":request.getSession().setAttribute("userShoppings",cookie.getValue());break;
                    }
                }
            }
        }
        return true;
    }

}
