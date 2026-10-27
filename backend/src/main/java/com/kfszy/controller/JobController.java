package com.kfszy.controller;

import com.kfszy.dao.ApplicationDao;
import com.kfszy.dao.JobDao;
import com.kfszy.dao.StudentDao;
import com.kfszy.entity.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/jobs")
public class JobController {
    private final JobDao jobDao;
    private final ApplicationDao applicationDao;
    private final StudentDao studentDao;

    public JobController(JobDao jobDao, ApplicationDao applicationDao, StudentDao studentDao) {
        this.jobDao = jobDao;
        this.applicationDao = applicationDao;
        this.studentDao = studentDao;
    }

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String keyword) {
        return Result.ok(jobDao.listForStudent(keyword));
    }

    @PostMapping("/apply")
    public Result apply(@SessionAttribute("user") User user, @RequestBody Map<String, Object> params) {
        Integer jobId = (Integer) params.get("jobId");
        String summary = (String) params.get("summary");
        Job job = jobDao.findById(jobId);
        if (job == null || !"APPROVED".equals(job.getStatus())) {
            return Result.error("该岗位不存在或未通过审核");
        }
        Student s = studentDao.findByUserId(user.getId());
        if (s == null) {
            return Result.error("请先完善个人信息");
        }
        if (applicationDao.exists(jobId, s.getId())) {
            return Result.error("您已投递过该岗位，请勿重复投递");
        }
        Application a = new Application();
        a.setJobId(jobId);
        a.setStudentId(s.getId());
        a.setSummary(summary);
        applicationDao.insert(a);
        return Result.ok();
    }
}
