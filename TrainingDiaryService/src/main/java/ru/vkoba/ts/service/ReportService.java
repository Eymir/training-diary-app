package ru.vkoba.ts.service;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Ricoshet
 * Date: 03.12.13
 * Time: 0:33
 */
public class ReportService {
    JdbcTemplate jdbcTemplate;

    public long getTotal() {
        String query = "select count(*) from training_statistic";
        return jdbcTemplate.queryForInt(query);
    }

    public Map<String, Long> getDeviceIdCountMap() {
        Map<String, Long> result = new HashMap<String, Long>();
        String query = "select user_device_uid, count(user_device_uid) c from training_statistic group by user_device_uid";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        for (Map<String, Object> row : rows) {
            result.put((String) row.get("user_device_uid"), (Long) row.get("c"));
        }
        return result;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
