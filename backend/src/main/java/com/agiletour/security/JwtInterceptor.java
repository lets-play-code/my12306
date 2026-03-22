package com.agiletour.security;

import com.agiletour.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    // 不需要认证的路径
    private static final String[] EXCLUDE_PATHS = {
        "/api/sessions",      // 登录接口
        "/api/trains"          // 查询火车（不需要登录）
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // 检查是否是需要排除的路径
        for (String excludePath : EXCLUDE_PATHS) {
            if (path.equals(excludePath) || path.startsWith(excludePath + "/")) {
                return true;
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"未授权，请先登录\"}");
            return false;
        }

        String token = authHeader.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Token无效或已过期\"}");
            return false;
        }

        // 将用户 ID 存入请求属性，供后续使用
        long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        return true;
    }
}
