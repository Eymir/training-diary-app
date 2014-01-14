package ru.td.portal.rest;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.td.portal.domain.UserData;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 1/14/14
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientDbServiceImpl implements ClientDbService {
    String basePath;

    @Override
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public
    @ResponseBody
    String uploadClientDb(@RequestParam("userData") UserData userData, @RequestParam("file") byte[] file) {
//        FileOutputStream fos = new FileOutputStream(new File(userData.getGoogleAuthToken()+"_db"));
//            IOUtils.write(file,fos);
        return "10";

    }


    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
