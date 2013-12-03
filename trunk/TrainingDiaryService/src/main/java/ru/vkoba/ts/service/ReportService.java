package ru.vkoba.ts.service;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ricoshet
 * Date: 03.12.13
 * Time: 0:33
 * To change this template use File | Settings | File Templates.
 */
public class ReportService {
    JdbcTemplate jdbcTemplate;

    public long getTotal() {
        String query = "select count(*) from training_statistic";
        return jdbcTemplate.queryForInt(query);
    }

    public Map<String, Integer> getDeviceIdCountMap() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        String query = "select user_device_uid, count(user_device_uid) c from training_statistic group by user_device_uid";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        for (Map<String, Object> row : rows) {
            result.put((String) row.get("user_device_uid"), (Integer) row.get("c")) ;
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
