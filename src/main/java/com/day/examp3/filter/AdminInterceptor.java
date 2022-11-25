package com.day.examp3.filter;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminInterceptor  implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Integer isAdmin;
        try{
           isAdmin  = (Integer) request.getSession().getAttribute("isAdmin");
        }catch (ClassCastException e){
            isAdmin = Integer.parseInt((String)request.getSession().getAttribute("isAdmin"));
        }
        if(isAdmin==null || isAdmin==0){
            response.sendRedirect("/admin/login");
            return false;
        }else return true;
    }
}
