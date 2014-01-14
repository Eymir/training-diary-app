package ru.td.portal;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.td.portal.domain.UserData;
import ru.td.portal.service.FolderGeneratorService;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 1/14/14
 * Time: 6:40 PM
 * To change this template use File | Settings | File Templates.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
        "classpath:spring/test-context.xml")
public class DbFolderGeneratorTest {
    @Autowired
    FolderGeneratorService folderGeneratorService;


    @Before
    public void cleanDirectory() throws IOException {
        File baseDir = new File(folderGeneratorService.getBaseFolderPath());
        for(File dir:baseDir.listFiles()){
            FileUtils.deleteDirectory(dir);
        }
    }
    @Test
    public void testCounter() {

     //Для теста в папке с номером может быть максимум две папки
     UserData userData = new UserData();
     userData.setGoogleAuthToken("user1");
     assertTrue(folderGeneratorService.generateFolderPath(userData).equals(folderGeneratorService.getBaseFolderPath()+"\\1\\user1"));

     userData = new UserData();
     userData.setGoogleAuthToken("user2");
     assertTrue(folderGeneratorService.generateFolderPath(userData).equals(folderGeneratorService.getBaseFolderPath()+"\\1\\user2"));

     userData = new UserData();
     userData.setGoogleAuthToken("user3");
     assertTrue(folderGeneratorService.generateFolderPath(userData).equals(folderGeneratorService.getBaseFolderPath()+"\\2\\user3"));
    }
}
