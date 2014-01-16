package ru.td.portal.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.td.portal.domain.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 1/15/14
 * Time: 11:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserDataRepository {
    JdbcTemplate jdbcTemplate;

    public void saveUserData(UserData userData) {
        String sqlInsert = "insert into UserData(registration_id,registration_channel,email,db_path) values (?,?,?,?)";
        String sqlUpdate = "update UserData set registration_id=?,registration_channel=?,email=?,db_path=?";
        UserData old = getUserDataByRegId(userData.getRegistrationId());
        if (old == null) {
            jdbcTemplate.update(sqlInsert, new Object[]{userData.getRegistrationId(), userData.getRegistrationChannel(), userData.getEmail(), userData.getDbPath()});
        } else {
            jdbcTemplate.update(sqlUpdate, new Object[]{userData.getRegistrationId(), userData.getRegistrationChannel(), userData.getEmail(), userData.getDbPath()});
        }
        System.out.println(getAllUserData());

    }

    public List<UserData> getAllUserData() {
        return jdbcTemplate.query("select * from UserData", new UserDataMapper());
    }

    public UserData getUserDataByRegId(String regId) {
        String sql = "select * from UserData where registration_id=?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{regId}, new UserDataMapper());
        } catch (DataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}


class UserDataMapper implements RowMapper<UserData> {
    @Override
    public UserData mapRow(ResultSet resultSet, int i) throws SQLException {
        UserData userData = new UserData();
        userData.setId(resultSet.getInt("id"));
        userData.setRegistrationId(resultSet.getString("registration_id"));
        userData.setRegistrationChannel(resultSet.getString("registration_channel"));
        userData.setEmail(resultSet.getString("email"));
        userData.setDbPath(resultSet.getString("db_path"));
        return userData;
    }
}