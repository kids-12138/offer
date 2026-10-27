package com.kfszy.dao;

import com.kfszy.entity.Enterprise;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class EnterpriseDao {
    private final JdbcTemplate jdbcTemplate;

    public EnterpriseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Enterprise findByUserId(Integer userId) {
        List<Enterprise> list = jdbcTemplate.query("SELECT * FROM enterprise WHERE user_id = ?",
                new BeanPropertyRowMapper<>(Enterprise.class), userId);
        return list.isEmpty() ? null : list.get(0);
    }

    public Enterprise findById(Integer id) {
        List<Enterprise> list = jdbcTemplate.query("SELECT * FROM enterprise WHERE id = ?",
                new BeanPropertyRowMapper<>(Enterprise.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(Enterprise e) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO enterprise(user_id, name, industry, address, contact, phone, status) VALUES(?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, e.getUserId());
            ps.setString(2, e.getName());
            ps.setString(3, e.getIndustry());
            ps.setString(4, e.getAddress());
            ps.setString(5, e.getContact());
            ps.setString(6, e.getPhone());
            ps.setString(7, e.getStatus());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void update(Enterprise e) {
        jdbcTemplate.update("UPDATE enterprise SET name=?, industry=?, address=?, contact=?, phone=? WHERE id=?",
                e.getName(), e.getIndustry(), e.getAddress(), e.getContact(), e.getPhone(), e.getId());
    }

    public void updateStatus(Integer id, String status) {
        jdbcTemplate.update("UPDATE enterprise SET status = ? WHERE id = ?", status, id);
    }

    public void deleteByUserId(Integer userId) {
        jdbcTemplate.update("DELETE FROM enterprise WHERE user_id = ?", userId);
    }

    public List<Map<String, Object>> list(String keyword) {
        String like = "%" + (keyword == null ? "" : keyword) + "%";
        return jdbcTemplate.queryForList(
                "SELECT e.*, u.username, u.status AS user_status FROM enterprise e JOIN `user` u ON e.user_id = u.id " +
                "WHERE e.name LIKE ? OR e.industry LIKE ? ORDER BY e.id DESC", like, like);
    }
}
