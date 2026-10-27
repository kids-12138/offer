package com.kfszy.controller;

import com.kfszy.dao.ApplicationDao;
import com.kfszy.dao.EnterpriseDao;
import com.kfszy.dao.JobDao;
import com.kfszy.entity.Application;
import com.kfszy.entity.Enterprise;
import com.kfszy.entity.Job;
import com.kfszy.entity.Result;
import com.kfszy.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ent")
public class EntController {
    private final EnterpriseDao enterpriseDao;
    private final JobDao jobDao;
    private final ApplicationDao applicationDao;

    public EntController(EnterpriseDao enterpriseDao, JobDao jobDao, ApplicationDao applicationDao) {
        this.enterpriseDao = enterpriseDao;
        this.jobDao = jobDao;
        this.applicationDao = applicationDao;
    }

    private Enterprise currentEnt(User user) {
        return enterpriseDao.findByUserId(user.getId());
    }

    @GetMapping("/profile")
    public Result profile(@SessionAttribute("user") User user) {
        Enterprise e = currentEnt(user);
        if (e == null) {
            e = new Enterprise();
            e.setUserId(user.getId());
        }
        return Result.ok(e);
    }

    @PostMapping("/profile/update")
    public Result updateProfile(@SessionAttribute("user") User user, @RequestBody Enterprise param) {
        Enterprise e = currentEnt(user);
        if (e == null) {
            return Result.error("企业信息不存在");
        }
        e.setName(param.getName());
        e.setIndustry(param.getIndustry());
        e.setAddress(param.getAddress());
        e.setContact(param.getContact());
        e.setPhone(param.getPhone());
        enterpriseDao.update(e);
        return Result.ok();
    }

    @GetMapping("/jobs")
    public Result jobs(@SessionAttribute("user") User user,
                       @RequestParam(required = false) String keyword) {
        Enterprise e = currentEnt(user);
        if (e == null) {
            return Result.ok(java.util.Collections.emptyList());
        }
        return Result.ok(jobDao.listByEnt(e.getId(), keyword));
    }

    @PostMapping("/jobs/save")
    public Result saveJob(@SessionAttribute("user") User user, @RequestBody Job param) {
        Enterprise e = currentEnt(user);
        if (e == null) {
            return Result.error("企业信息不存在");
        }
        if (param.getTitle() == null || param.getTitle().trim().isEmpty()) {
            return Result.error("岗位名称不能为空");
        }
        if (param.getId() == null) {
            Job j = new Job();
            j.setEnterpriseId(e.getId());
            j.setTitle(param.getTitle());
            j.setSalary(param.getSalary());
            j.setCity(param.getCity());
            j.setDescription(param.getDescription());
            j.setStatus("PENDING");
            jobDao.insert(j);
        } else {
            Job exist = jobDao.findById(param.getId());
            if (exist == null || !exist.getEnterpriseId().equals(e.getId())) {
                return Result.error("岗位不存在");
            }
            exist.setTitle(param.getTitle());
            exist.setSalary(param.getSalary());
            exist.setCity(param.getCity());
            exist.setDescription(param.getDescription());
            jobDao.update(exist);
        }
        return Result.ok();
    }

    @PostMapping("/jobs/delete")
    public Result deleteJob(@SessionAttribute("user") User user, @RequestBody Map<String, Integer> params) {
        Enterprise e = currentEnt(user);
        Job j = jobDao.findById(params.get("id"));
        if (e == null || j == null || !j.getEnterpriseId().equals(e.getId())) {
            return Result.error("岗位不存在");
        }
        applicationDao.deleteByJob(j.getId());
        jobDao.delete(j.getId());
        return Result.ok();
    }

    @GetMapping("/applications")
    public Result applications(@SessionAttribute("user") User user) {
        Enterprise e = currentEnt(user);
        if (e == null) {
            return Result.ok(java.util.Collections.emptyList());
        }
        return Result.ok(applicationDao.listByEnt(e.getId()));
    }

    @PostMapping("/applications/handle")
    public Result handleApplication(@SessionAttribute("user") User user, @RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        String status = (String) params.get("status");
        if (!"ACCEPTED".equals(status) && !"REJECTED".equals(status)) {
            return Result.error("非法操作");
        }
        Enterprise e = currentEnt(user);
        Application a = applicationDao.findById(id);
        if (e == null || a == null) {
            return Result.error("投递记录不存在");
        }
        Job j = jobDao.findById(a.getJobId());
        if (j == null || !j.getEnterpriseId().equals(e.getId())) {
            return Result.error("无权处理该投递");
        }
        if (!"SUBMITTED".equals(a.getStatus())) {
            return Result.error("该投递已处理");
        }
        applicationDao.updateStatus(id, status);
        return Result.ok();
    }
}
