package com.kfszy.dao;

import com.kfszy.entity.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findByUsername(String username) {
        List<User> list = jdbcTemplate.query("SELECT * FROM `user` WHERE username = ?",
                new BeanPropertyRowMapper<>(User.class), username);
        return list.isEmpty() ? null : list.get(0);
    }

    public User findById(Integer id) {
        List<User> list = jdbcTemplate.query("SELECT * FROM `user` WHERE id = ?",
                new BeanPropertyRowMapper<>(User.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO `user`(username, password, role) VALUES(?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void updateStatus(Integer id, Integer status) {
        jdbcTemplate.update("UPDATE `user` SET status = ? WHERE id = ?", status, id);
    }

    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM `user` WHERE id = ?", id);
    }
}
