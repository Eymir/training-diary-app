package ru.vkoba.ts.service;


import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.vkoba.ts.domain.StatisticElement;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Date;

/**
 * User: vkoba_000
 * Date: 12/2/13
 * Time: 6:00 PM
 */

@Path("/tds")
@Scope
public class TrainingService {
    JdbcTemplate trainingServiceDao;

    @POST
    //@Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/print")
    @Transactional
    public Response addStatistic(StatisticElement element) {

        String query = "insert into TrainingStatistic (user_device_uid, trainingstamp_start, trainingstamp_end) values(?,?,?)";
        trainingServiceDao.update(query, new Object[]{element.getDeviceId(), element.getTrainingStart(), element.getTrainingEnd()});
        return Response.status(200).entity("OK").build();
    }


    public JdbcTemplate getTrainingServiceDao() {
        return trainingServiceDao;
    }

    public void setTrainingServiceDao(JdbcTemplate trainingServiceDao) {
        this.trainingServiceDao = trainingServiceDao;
    }
}
