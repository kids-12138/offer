package com.kfszy.controller;

import com.kfszy.dao.EmploymentDao;
import com.kfszy.dao.StudentDao;
import com.kfszy.entity.Employment;
import com.kfszy.entity.Result;
import com.kfszy.entity.Student;
import com.kfszy.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentDao studentDao;
    private final EmploymentDao employmentDao;

    public StudentController(StudentDao studentDao, EmploymentDao employmentDao) {
        this.studentDao = studentDao;
        this.employmentDao = employmentDao;
    }

    @GetMapping("/profile")
    public Result profile(@SessionAttribute("user") User user) {
        Student s = studentDao.findByUserId(user.getId());
        if (s == null) {
            s = new Student();
            s.setUserId(user.getId());
        }
        return Result.ok(s);
    }

    @PostMapping("/profile/update")
    public Result updateProfile(@SessionAttribute("user") User user, @RequestBody Student param) {
        Student s = studentDao.findByUserId(user.getId());
        if (s == null) {
            s = new Student();
            s.setUserId(user.getId());
            s.setName(param.getName());
            s.setId(studentDao.insert(s));
        }
        s.setStudentNo(param.getStudentNo());
        s.setName(param.getName());
        s.setGender(param.getGender());
        s.setCollege(param.getCollege());
        s.setMajor(param.getMajor());
        s.setClassName(param.getClassName());
        s.setGrade(param.getGrade());
        s.setPhone(param.getPhone());
        s.setEmail(param.getEmail());
        studentDao.update(s);
        return Result.ok();
    }

    @GetMapping("/employment")
    public Result employment(@SessionAttribute("user") User user) {
        Student s = studentDao.findByUserId(user.getId());
        if (s == null) {
            return Result.ok(null);
        }
        return Result.ok(employmentDao.findByStudentId(s.getId()));
    }

    @PostMapping("/employment/save")
    public Result saveEmployment(@SessionAttribute("user") User user, @RequestBody Employment param) {
        Student s = studentDao.findByUserId(user.getId());
        if (s == null) {
            return Result.error("请先完善个人信息");
        }
        if (param.getType() == null || param.getType().isEmpty()) {
            return Result.error("请选择去向类型");
        }
        param.setStudentId(s.getId());
        employmentDao.upsert(param);
        return Result.ok();
    }
}
