package com.kfszy.dao;

import com.kfszy.entity.Job;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class JobDao {
    private final JdbcTemplate jdbcTemplate;

    public JobDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insert(Job j) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO job(enterprise_id, title, salary, city, description, status) VALUES(?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, j.getEnterpriseId());
            ps.setString(2, j.getTitle());
            ps.setString(3, j.getSalary());
            ps.setString(4, j.getCity());
            ps.setString(5, j.getDescription());
            ps.setString(6, j.getStatus());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void update(Job j) {
        jdbcTemplate.update("UPDATE job SET title=?, salary=?, city=?, description=? WHERE id=?",
                j.getTitle(), j.getSalary(), j.getCity(), j.getDescription(), j.getId());
    }

    public void updateStatus(Integer id, String status) {
        jdbcTemplate.update("UPDATE job SET status = ? WHERE id = ?", status, id);
    }

    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM job WHERE id = ?", id);
    }

    public Job findById(Integer id) {
        List<Job> list = jdbcTemplate.query("SELECT * FROM job WHERE id = ?",
                new BeanPropertyRowMapper<>(Job.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Map<String, Object>> listForStudent(String keyword) {
        String like = "%" + (keyword == null ? "" : keyword) + "%";
        return jdbcTemplate.queryForList(
                "SELECT j.id, j.enterprise_id, j.title, j.salary, j.city, j.description, j.status, e.name AS ent_name, e.industry, DATE_FORMAT(j.create_time, '%Y-%m-%d %H:%i') AS create_time FROM job j JOIN enterprise e ON j.enterprise_id = e.id " +
                "WHERE j.status = 'APPROVED' AND (j.title LIKE ? OR e.name LIKE ? OR j.city LIKE ?) ORDER BY j.create_time DESC",
                like, like, like);
    }

    public List<Map<String, Object>> listAll(String keyword, String status) {
        String like = "%" + (keyword == null ? "" : keyword) + "%";
        StringBuilder sql = new StringBuilder(
                "SELECT j.id, j.enterprise_id, j.title, j.salary, j.city, j.description, j.status, e.name AS ent_name, DATE_FORMAT(j.create_time, '%Y-%m-%d %H:%i') AS create_time FROM job j JOIN enterprise e ON j.enterprise_id = e.id WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (j.title LIKE ? OR e.name LIKE ?)");
            args.add(like);
            args.add(like);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND j.status = ?");
            args.add(status);
        }
        sql.append(" ORDER BY j.create_time DESC");
        return jdbcTemplate.queryForList(sql.toString(), args.toArray());
    }

    public List<Map<String, Object>> listByEnt(Integer entId, String keyword) {
        String like = "%" + (keyword == null ? "" : keyword) + "%";
        return jdbcTemplate.queryForList(
                "SELECT j.id, j.enterprise_id, j.title, j.salary, j.city, j.description, j.status, e.name AS ent_name, DATE_FORMAT(j.create_time, '%Y-%m-%d %H:%i') AS create_time FROM job j JOIN enterprise e ON j.enterprise_id = e.id " +
                "WHERE j.enterprise_id = ? AND (j.title LIKE ?) ORDER BY j.create_time DESC", entId, like);
    }
}
