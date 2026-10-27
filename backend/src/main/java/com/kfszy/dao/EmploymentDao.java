package com.kfszy.dao;

import com.kfszy.entity.Employment;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class EmploymentDao {
    private final JdbcTemplate jdbcTemplate;

    public EmploymentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Employment findByStudentId(Integer studentId) {
        List<Employment> list = jdbcTemplate.query("SELECT * FROM employment WHERE student_id = ?",
                new BeanPropertyRowMapper<>(Employment.class), studentId);
        return list.isEmpty() ? null : list.get(0);
    }

    public void upsert(Employment e) {
        jdbcTemplate.update(
                "INSERT INTO employment(student_id, type, company, position, salary, city, remark) VALUES(?,?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE type=VALUES(type), company=VALUES(company), position=VALUES(position), " +
                "salary=VALUES(salary), city=VALUES(city), remark=VALUES(remark)",
                e.getStudentId(), e.getType(), e.getCompany(), e.getPosition(), e.getSalary(), e.getCity(), e.getRemark());
    }

    public List<Map<String, Object>> list(String keyword) {
        String like = "%" + (keyword == null ? "" : keyword) + "%";
        return jdbcTemplate.queryForList(
                "SELECT em.id, em.student_id, em.type, em.company, em.position, em.salary, em.city, em.remark, " +
                "DATE_FORMAT(em.update_time, '%Y-%m-%d %H:%i') AS update_time, " +
                "s.name AS student_name, s.student_no, s.college, s.major FROM employment em " +
                "JOIN student s ON em.student_id = s.id " +
                "WHERE s.name LIKE ? OR s.student_no LIKE ? OR s.college LIKE ? ORDER BY em.update_time DESC",
                like, like, like);
    }

    public void deleteByStudentId(Integer studentId) {
        jdbcTemplate.update("DELETE FROM employment WHERE student_id = ?", studentId);
    }

    public int countStudents() {
        Integer c = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM student", Integer.class);
        return c == null ? 0 : c;
    }

    public List<Map<String, Object>> typeDist() {
        return jdbcTemplate.queryForList("SELECT type AS name, COUNT(*) AS value FROM employment GROUP BY type");
    }

    public List<Map<String, Object>> cityDist() {
        return jdbcTemplate.queryForList(
                "SELECT city AS name, COUNT(*) AS value FROM employment " +
                "WHERE type = 'EMPLOYED' AND city IS NOT NULL AND city <> '' " +
                "GROUP BY city ORDER BY value DESC LIMIT 10");
    }

    public List<Map<String, Object>> salaryDist() {
        return jdbcTemplate.queryForList(
                "SELECT CASE WHEN salary < 5000 THEN '5千以下' WHEN salary < 8000 THEN '5-8千' " +
                "WHEN salary < 12000 THEN '8千-1.2万' WHEN salary < 20000 THEN '1.2万-2万' ELSE '2万以上' END AS name, " +
                "COUNT(*) AS value FROM employment WHERE type = 'EMPLOYED' AND salary IS NOT NULL GROUP BY name");
    }

    public List<Map<String, Object>> collegeStats() {
        return jdbcTemplate.queryForList(
                "SELECT s.college AS name, COUNT(*) AS total, SUM(em.type = 'EMPLOYED') AS employed " +
                "FROM employment em JOIN student s ON em.student_id = s.id " +
                "WHERE s.college IS NOT NULL AND s.college <> '' GROUP BY s.college");
    }
}
