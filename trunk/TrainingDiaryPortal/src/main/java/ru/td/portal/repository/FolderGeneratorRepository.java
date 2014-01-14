package ru.td.portal.repository;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 1/14/14
 * Time: 6:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderGeneratorRepository {
    JdbcTemplate jdbcTemplate;

    public int getCount() {
        return jdbcTemplate.queryForInt("select counter from FolderGenerator");
    }

    public void incrementCount() {
        String sql = "UPDATE FolderGenerator SET counter=?";
        int newCount = getCount() + 1;
        jdbcTemplate.update(sql, new Object[]{newCount});
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
