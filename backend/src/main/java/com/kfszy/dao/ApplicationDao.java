package com.kfszy.dao;

import com.kfszy.entity.Application;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ApplicationDao {
    private final JdbcTemplate jdbcTemplate;

    public ApplicationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean exists(Integer jobId, Integer studentId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM application WHERE job_id = ? AND student_id = ?", Integer.class, jobId, studentId);
        return count != null && count > 0;
    }

    public void insert(Application a) {
        jdbcTemplate.update("INSERT INTO application(job_id, student_id, summary) VALUES(?,?,?)",
                a.getJobId(), a.getStudentId(), a.getSummary());
    }

    public Application findById(Integer id) {
        List<Application> list = jdbcTemplate.query("SELECT * FROM application WHERE id = ?",
                new BeanPropertyRowMapper<>(Application.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM application WHERE id = ?", id);
    }

    public void deleteByJob(Integer jobId) {
        jdbcTemplate.update("DELETE FROM application WHERE job_id = ?", jobId);
    }

    public void updateStatus(Integer id, String status) {
        jdbcTemplate.update("UPDATE application SET status = ? WHERE id = ?", status, id);
    }

    public List<Map<String, Object>> listByStudent(Integer studentId) {
        return jdbcTemplate.queryForList(
                "SELECT a.id, a.status, a.summary, DATE_FORMAT(a.create_time, '%Y-%m-%d %H:%i') AS create_time, j.title, j.city, j.salary, e.name AS ent_name " +
                "FROM application a JOIN job j ON a.job_id = j.id JOIN enterprise e ON j.enterprise_id = e.id " +
                "WHERE a.student_id = ? ORDER BY a.create_time DESC", studentId);
    }

    public List<Map<String, Object>> listByEnt(Integer entId) {
        return jdbcTemplate.queryForList(
                "SELECT a.id, a.status, a.summary, DATE_FORMAT(a.create_time, '%Y-%m-%d %H:%i') AS create_time, j.title AS job_title, s.name AS student_name, " +
                "s.student_no, s.major, s.college, s.phone " +
                "FROM application a JOIN job j ON a.job_id = j.id JOIN student s ON a.student_id = s.id " +
                "WHERE j.enterprise_id = ? ORDER BY a.create_time DESC", entId);
    }
}
