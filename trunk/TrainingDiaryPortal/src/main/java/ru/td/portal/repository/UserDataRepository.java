package ru.td.portal.repository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.td.portal.domain.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * User: vkoba_000
 * Date: 1/15/14
 * Time: 11:39 AM
 */
public class UserDataRepository {
    private static Logger log = LoggerFactory.getLogger(UserDataRepository.class);
    JdbcTemplate jdbcTemplate;

    public UserData saveUserData(UserData userData) {
        String sqlInsert = "insert into UserData(registration_id, registration_date, registration_channel,email,db_path) values (?,?,?,?,?)";
        String sqlUpdate = "update UserData set registration_id=?,registration_channel=?,email=?,db_path=? where id = ?";
        UserData old = getUserDataByRegIdAndChannel(userData.getRegistrationId(), userData.getRegistrationChannel());
        if (old == null) {
            jdbcTemplate.update(sqlInsert,
                    new Object[]{userData.getRegistrationId(), new Date().getTime(), userData.getRegistrationChannel(), userData.getEmail(), userData.getDbPath()});
        } else {
            jdbcTemplate.update(sqlUpdate,
                    new Object[]{userData.getRegistrationId(), userData.getRegistrationChannel(), userData.getEmail(), userData.getDbPath(), old.getId()});
        }
        return getUserDataByRegIdAndChannel(userData.getRegistrationId(), userData.getRegistrationChannel());
    }

    public List<UserData> getAllUserData() {
        return jdbcTemplate.query("select * from UserData", new UserDataMapper());
    }

    public UserData getUserDataByRegIdAndChannel(String regId, String channel) {
        String query = "select * from UserData where registration_id=? and registration_channel=?";

        try {
            List<UserData> dataList = jdbcTemplate.query(query, new Object[]{regId, channel}, new UserDataMapper());
            if (dataList.size() > 1) {
                throw new RuntimeException("for UserData with regId=" + regId + ", and channel=" + channel + " exists more than 1 records");
            }
            if (dataList.size() == 0) {
                return null;
            }
            return dataList.get(0);

        } catch (Exception e) {
            log.error("Error getUserData! Details:", e);
            throw new RuntimeException(e);
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
