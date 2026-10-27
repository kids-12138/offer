package com.kfszy.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfszy.entity.Result;
import com.kfszy.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return reject(response, "请先登录");
        }
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String required = null;
        if (path.startsWith("/api/student")) required = "STUDENT";
        else if (path.startsWith("/api/ent")) required = "ENTERPRISE";
        else if (path.startsWith("/api/admin")) required = "ADMIN";
        if (required != null && !required.equals(user.getRole())) {
            return reject(response, "无权限访问");
        }
        return true;
    }

    private boolean reject(HttpServletResponse response, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(new ObjectMapper().writeValueAsString(new Result(401, msg, null)));
        }
        return false;
    }
}
