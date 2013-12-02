package ru.vkoba.ts.service;


import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.vkoba.ts.domain.StatisticElement;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: vkoba_000
 * Date: 12/2/13
 * Time: 6:00 PM
 */

@Path("/tds")
@Scope
public class TrainingService {
    JdbcTemplate jdbcTemplate;

    @POST
    //@Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/print")
    @Transactional
    public Response addStatistic(StatisticElement element) {
        String query = "insert into TrainingStatistic (user_device_uid, trainingstamp_start, trainingstamp_end) values(?,?,?)";
        jdbcTemplate.update(query, new Object[]{element.getDeviceId(), element.getTrainingStart(), element.getTrainingEnd()});
        return Response.status(200).entity("OK").build();
    }

    public Map<String, Long> getStatisticByDevicesForPeriod(Date from, Date to) {
        Map<String, Long> result = new HashMap<String, Long>();
        return result;
    }

    public long getTotal() {
        return -1;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate trainingServiceDao) {
        this.jdbcTemplate = trainingServiceDao;
    }
}
