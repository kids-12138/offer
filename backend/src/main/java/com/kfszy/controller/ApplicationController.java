package com.kfszy.controller;

import com.kfszy.dao.ApplicationDao;
import com.kfszy.dao.StudentDao;
import com.kfszy.entity.Application;
import com.kfszy.entity.Result;
import com.kfszy.entity.Student;
import com.kfszy.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/applications")
public class ApplicationController {
    private final ApplicationDao applicationDao;
    private final StudentDao studentDao;

    public ApplicationController(ApplicationDao applicationDao, StudentDao studentDao) {
        this.applicationDao = applicationDao;
        this.studentDao = studentDao;
    }

    @GetMapping("/list")
    public Result list(@SessionAttribute("user") User user) {
        Student s = studentDao.findByUserId(user.getId());
        if (s == null) {
            return Result.ok(java.util.Collections.emptyList());
        }
        return Result.ok(applicationDao.listByStudent(s.getId()));
    }

    @PostMapping("/cancel")
    public Result cancel(@SessionAttribute("user") User user, @RequestBody Map<String, Integer> params) {
        Application a = applicationDao.findById(params.get("id"));
        Student s = studentDao.findByUserId(user.getId());
        if (a == null || s == null || !a.getStudentId().equals(s.getId())) {
            return Result.error("投递记录不存在");
        }
        if (!"SUBMITTED".equals(a.getStatus())) {
            return Result.error("该投递已被处理，无法撤回");
        }
        applicationDao.delete(a.getId());
        return Result.ok();
    }
}
