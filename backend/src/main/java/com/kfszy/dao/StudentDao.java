package com.kfszy.dao;

import com.kfszy.entity.Student;
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
public class StudentDao {
    private final JdbcTemplate jdbcTemplate;

    public StudentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Student findByUserId(Integer userId) {
        List<Student> list = jdbcTemplate.query("SELECT * FROM student WHERE user_id = ?",
                new BeanPropertyRowMapper<>(Student.class), userId);
        return list.isEmpty() ? null : list.get(0);
    }

    public Student findById(Integer id) {
        List<Student> list = jdbcTemplate.query("SELECT * FROM student WHERE id = ?",
                new BeanPropertyRowMapper<>(Student.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(Student s) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO student(user_id, student_no, name, gender, college, major, class_name, grade, phone, email) VALUES(?,?,?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, s.getUserId());
            ps.setString(2, s.getStudentNo());
            ps.setString(3, s.getName());
            ps.setString(4, s.getGender());
            ps.setString(5, s.getCollege());
            ps.setString(6, s.getMajor());
            ps.setString(7, s.getClassName());
            ps.setString(8, s.getGrade());
            ps.setString(9, s.getPhone());
            ps.setString(10, s.getEmail());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void update(Student s) {
        jdbcTemplate.update("UPDATE student SET student_no=?, name=?, gender=?, college=?, major=?, class_name=?, grade=?, phone=?, email=? WHERE id=?",
                s.getStudentNo(), s.getName(), s.getGender(), s.getCollege(), s.getMajor(),
                s.getClassName(), s.getGrade(), s.getPhone(), s.getEmail(), s.getId());
    }

    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM student WHERE id = ?", id);
    }

    public List<Map<String, Object>> list(String keyword) {
        String like = "%" + (keyword == null ? "" : keyword) + "%";
        return jdbcTemplate.queryForList(
                "SELECT s.*, u.username, u.status AS user_status FROM student s JOIN `user` u ON s.user_id = u.id " +
                "WHERE s.name LIKE ? OR s.student_no LIKE ? OR s.college LIKE ? ORDER BY s.id DESC", like, like, like);
    }

    public void deleteByUserId(Integer userId) {
        jdbcTemplate.update("DELETE FROM student WHERE user_id = ?", userId);
    }
}
