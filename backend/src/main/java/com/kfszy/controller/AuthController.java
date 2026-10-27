package com.kfszy.controller;

import com.kfszy.dao.EnterpriseDao;
import com.kfszy.dao.StudentDao;
import com.kfszy.dao.UserDao;
import com.kfszy.entity.Enterprise;
import com.kfszy.entity.Result;
import com.kfszy.entity.Student;
import com.kfszy.entity.User;
import com.kfszy.util.MD5Util;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserDao userDao;
    private final StudentDao studentDao;
    private final EnterpriseDao enterpriseDao;

    public AuthController(UserDao userDao, StudentDao studentDao, EnterpriseDao enterpriseDao) {
        this.userDao = userDao;
        this.studentDao = studentDao;
        this.enterpriseDao = enterpriseDao;
    }

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params, HttpSession session) {
        String username = params.get("username");
        String password = params.get("password");
        User user = userDao.findByUsername(username);
        if (user == null || !user.getPassword().equals(MD5Util.md5(password))) {
            return Result.error("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }
        if ("ENTERPRISE".equals(user.getRole())) {
            Enterprise ent = enterpriseDao.findByUserId(user.getId());
            if (ent != null) {
                if ("PENDING".equals(ent.getStatus())) {
                    return Result.error("企业账号正在审核中，请等待管理员审核");
                }
                if ("REJECTED".equals(ent.getStatus())) {
                    return Result.error("企业审核未通过，无法登录");
                }
            }
        }
        session.setAttribute("user", user);
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("role", user.getRole());
        return Result.ok(info);
    }

    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String role = params.get("role");
        String name = params.get("name");
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return Result.error("用户名和密码不能为空");
        }
        if (!"STUDENT".equals(role) && !"ENTERPRISE".equals(role)) {
            return Result.error("注册角色不正确");
        }
        if (name == null || name.trim().isEmpty()) {
            return Result.error("名称不能为空");
        }
        if (userDao.findByUsername(username) != null) {
            return Result.error("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(MD5Util.md5(password));
        user.setRole(role);
        int userId = userDao.insert(user);
        if ("STUDENT".equals(role)) {
            Student s = new Student();
            s.setUserId(userId);
            s.setName(name);
            studentDao.insert(s);
        } else {
            Enterprise e = new Enterprise();
            e.setUserId(userId);
            e.setName(name);
            e.setStatus("PENDING");
            enterpriseDao.insert(e);
        }
        return Result.ok();
    }

    @GetMapping("/me")
    public Result me(@SessionAttribute("user") User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("role", user.getRole());
        if ("STUDENT".equals(user.getRole())) {
            Student s = studentDao.findByUserId(user.getId());
            info.put("name", s == null ? user.getUsername() : s.getName());
        } else if ("ENTERPRISE".equals(user.getRole())) {
            Enterprise e = enterpriseDao.findByUserId(user.getId());
            info.put("name", e == null ? user.getUsername() : e.getName());
        } else {
            info.put("name", "管理员");
        }
        return Result.ok(info);
    }

    @PostMapping("/logout")
    public Result logout(HttpSession session) {
        session.invalidate();
        return Result.ok();
    }
}
