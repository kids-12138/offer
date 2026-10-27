package com.kfszy.controller;

import com.kfszy.dao.*;
import com.kfszy.entity.Result;
import com.kfszy.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserDao userDao;
    private final StudentDao studentDao;
    private final EnterpriseDao enterpriseDao;
    private final JobDao jobDao;
    private final EmploymentDao employmentDao;

    public AdminController(UserDao userDao, StudentDao studentDao, EnterpriseDao enterpriseDao,
                           JobDao jobDao, EmploymentDao employmentDao) {
        this.userDao = userDao;
        this.studentDao = studentDao;
        this.enterpriseDao = enterpriseDao;
        this.jobDao = jobDao;
        this.employmentDao = employmentDao;
    }

    @GetMapping("/students")
    public Result students(@RequestParam(required = false) String keyword) {
        return Result.ok(studentDao.list(keyword));
    }

    @PostMapping("/students/status")
    public Result studentStatus(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer status = (Integer) params.get("status");
        userDao.updateStatus(userId, status);
        return Result.ok();
    }

    @PostMapping("/students/delete")
    public Result deleteStudent(@RequestBody Map<String, Integer> params) {
        Integer userId = params.get("userId");
        User user = userDao.findById(userId);
        if (user == null || !"STUDENT".equals(user.getRole())) {
            return Result.error("学生不存在");
        }
        employmentDao.deleteByStudentId(studentDao.findByUserId(userId) == null ? -1 : studentDao.findByUserId(userId).getId());
        studentDao.deleteByUserId(userId);
        userDao.delete(userId);
        return Result.ok();
    }

    @GetMapping("/enterprises")
    public Result enterprises(@RequestParam(required = false) String keyword) {
        return Result.ok(enterpriseDao.list(keyword));
    }

    @PostMapping("/enterprises/audit")
    public Result auditEnterprise(@RequestBody Map<String, String> params) {
        Integer id = Integer.valueOf(params.get("id"));
        String status = params.get("status");
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            return Result.error("非法操作");
        }
        enterpriseDao.updateStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/jobs")
    public Result jobs(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status) {
        return Result.ok(jobDao.listAll(keyword, status));
    }

    @PostMapping("/jobs/audit")
    public Result auditJob(@RequestBody Map<String, String> params) {
        Integer id = Integer.valueOf(params.get("id"));
        String status = params.get("status");
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            return Result.error("非法操作");
        }
        jobDao.updateStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/employments")
    public Result employments(@RequestParam(required = false) String keyword) {
        return Result.ok(employmentDao.list(keyword));
    }

    @GetMapping("/stats/overview")
    public Result overview() {
        int total = employmentDao.countStudents();
        Map<String, Integer> typeCounts = new HashMap<>();
        for (Map<String, Object> row : employmentDao.typeDist()) {
            String type = (String) row.get("name");
            Integer value = ((Number) row.get("value")).intValue();
            typeCounts.put(type, value);
        }
        int employed = typeCounts.getOrDefault("EMPLOYED", 0);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalStudents", total);
        data.put("registered", typeCounts.values().stream().mapToInt(Integer::intValue).sum());
        data.put("employed", employed);
        data.put("furtherStudy", typeCounts.getOrDefault("FURTHER_STUDY", 0));
        data.put("entrepreneur", typeCounts.getOrDefault("ENTREPRENEUR", 0));
        data.put("unemployed", typeCounts.getOrDefault("UNEMPLOYED", 0));
        data.put("employmentRate", total == 0 ? 0 : Math.round(employed * 1000.0 / total) / 10.0);
        return Result.ok(data);
    }

    @GetMapping("/stats/type")
    public Result statsType() {
        return Result.ok(employmentDao.typeDist());
    }

    @GetMapping("/stats/city")
    public Result statsCity() {
        return Result.ok(employmentDao.cityDist());
    }

    @GetMapping("/stats/salary")
    public Result statsSalary() {
        java.util.List<Map<String, Object>> rows = employmentDao.salaryDist();
        java.util.List<String> order = java.util.Arrays.asList("5千以下", "5-8千", "8千-1.2万", "1.2万-2万", "2万以上");
        java.util.List<Map<String, Object>> sorted = new ArrayList<>();
        for (String name : order) {
            for (Map<String, Object> row : rows) {
                if (name.equals(row.get("name"))) {
                    sorted.add(row);
                }
            }
        }
        return Result.ok(sorted);
    }

    @GetMapping("/stats/college")
    public Result statsCollege() {
        return Result.ok(employmentDao.collegeStats());
    }
}
