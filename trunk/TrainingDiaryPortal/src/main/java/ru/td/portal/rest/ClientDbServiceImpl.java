package ru.td.portal.rest;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.td.portal.domain.UserData;
import ru.td.portal.service.FolderGeneratorService;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 1/14/14
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientDbServiceImpl implements ClientDbService {
    FolderGeneratorService folderGeneratorService;

    @Override
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public
    @ResponseBody
    String uploadClientDb(@RequestParam("userData") UserData userData, @RequestParam("file") byte[] file) {
        String result = "";
        String folderPath = folderGeneratorService.generateFolderPath(userData);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(folderPath + "\\" + "sqlitedb"));
            IOUtils.write(file, fos);
            result = folderPath + "\\" + "sqlitedb";
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fos);
        }

        return result;

    }

    public FolderGeneratorService getFolderGeneratorService() {
        return folderGeneratorService;
    }

    public void setFolderGeneratorService(FolderGeneratorService folderGeneratorService) {
        this.folderGeneratorService = folderGeneratorService;
    }
}
