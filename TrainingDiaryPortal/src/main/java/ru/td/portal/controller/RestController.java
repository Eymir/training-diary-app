package ru.td.portal.controller;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.td.portal.converter.UserDataConverter;
import ru.td.portal.domain.TransferData;
import ru.td.portal.domain.UserData;
import ru.td.portal.repository.UserDataRepository;
import ru.td.portal.service.FolderGeneratorService;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 1/15/14
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/api")
public class RestController {
    @Autowired
    UserDataRepository userDataRepository;
    @Autowired
    FolderGeneratorService folderGeneratorService;
    private Logger log = LoggerFactory.getLogger(RestController.class);


    @RequestMapping(value = "/uploadDb", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)

    public
    @ResponseBody
    Response uploadClientDb(@RequestBody TransferData transferData) {
        UserData userData = UserDataConverter.convertTransferDataObjectToUserData(transferData);
        UserData userDataFromDb = userDataRepository.getUserDataByRegIdAndChannel(userData.getRegistrationId(), userData.getRegistrationChannel());
        if (userDataFromDb == null) {
            userData.setId(userDataRepository.saveUserData(userData).getId());
        } else {
            userData.setId(userDataFromDb.getId());
        }
        String dbPath = folderGeneratorService.generateFolderPath(userData) + IOUtils.DIR_SEPARATOR + "db.sqlite";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(dbPath));
            IOUtils.write(Base64.decodeBase64(userData.getDb()), fos);
            userData.setDbPath(dbPath);
            userDataRepository.saveUserData(userData);
        } catch (IOException e) {
            log.error("Error upload database! Details:", e);
            return Response.status(500).entity("Error").build();
        } finally {
            IOUtils.closeQuietly(fos);
        }
        return Response.status(200).entity("OK").build();
    }

    @RequestMapping(value = "/downloadDb", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    public
    @ResponseBody
    Response downloadClientDb(@QueryParam("id") String id, @QueryParam("channel") String channel) {
        UserData userData = userDataRepository.getUserDataByRegIdAndChannel(id, channel);
        checkUserDataNotNull(id, channel, userData);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(userData.getDbPath()));
            userData.setRegistrationId(id);
            userData.setRegistrationChannel(channel);
            userData.setDb(Base64.encodeBase64String(IOUtils.toByteArray(fis)));
            return Response.status(200).entity(UserDataConverter.convertUserDataToTransferDataObject(userData)).build();
        } catch (IOException e) {
            log.error("Error download database! Details:", e);
            return Response.status(500).entity(e).build();
        } finally {
            IOUtils.closeQuietly(fis);
        }

    }



    private void checkUserDataNotNull(String id, String channel, UserData result) {
        if (result == null || result.getDbPath() == null) {
            throw new RuntimeException("Error! Record with id=" + id + " and channel=" + channel + " not found");
        }
    }

    public FolderGeneratorService getFolderGeneratorService() {
        return folderGeneratorService;
    }

    public void setFolderGeneratorService(FolderGeneratorService folderGeneratorService) {
        this.folderGeneratorService = folderGeneratorService;
    }

    public UserDataRepository getUserDataRepository() {
        return userDataRepository;
    }

    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }
}
