package com.kfszy.entity;

import java.util.Date;

public class Job {
    private Integer id;
    private Integer enterpriseId;
    private String title;
    private String salary;
    private String city;
    private String description;
    private String status;
    private Date createTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(Integer enterpriseId) { this.enterpriseId = enterpriseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
