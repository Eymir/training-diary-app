package ru.td.portal.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.td.portal.domain.UserData;
import ru.td.portal.repository.UserDataRepository;
import ru.td.portal.service.FolderGeneratorService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import java.io.File;
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
public class UploadDbRestController {
    private Logger log = LoggerFactory.getLogger(UploadDbRestController.class);
    UserDataRepository userDataRepository;
    FolderGeneratorService folderGeneratorService;

     //TODO:Костыль с возвращаемым типом, бул почему-то вернуть не получается
    @RequestMapping(value = "/uploadDb", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML)
    @ResponseBody
    public String uploadClientDb(@RequestBody UserData userData) {
        String dbPath = folderGeneratorService.generateFolderPath(userData) + IOUtils.DIR_SEPARATOR + "db.sqlite";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(dbPath));
            IOUtils.write(userData.getDb(), fos);
            userDataRepository.addUserData(userData);
        } catch (IOException e) {
            log.error("Error upload database! Details:", e);
            return "ERROR";
        } finally {
            IOUtils.closeQuietly(fos);
        }
        return "OK";

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
