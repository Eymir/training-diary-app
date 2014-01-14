package ru.td.portal.service;

import ru.td.portal.domain.UserData;
import ru.td.portal.repository.FolderGeneratorRepository;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 1/14/14
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderGeneratorService {
    String baseFolderPath;
    int maxFileInFolder;
    FolderGeneratorRepository folderGeneratorRepository;



    public String generateFolderPath(UserData userData) {
        int folderIndex = folderGeneratorRepository.getCount();
        File dir = new File(baseFolderPath + "\\" + folderIndex);
        createFolderIfNotExist(dir);

        if (dir.listFiles().length < maxFileInFolder) {
            createFolderIfNotExist(new File(baseFolderPath + "\\" + folderIndex + "\\" + userData.getGoogleAuthToken()));
            return baseFolderPath + "\\" + folderIndex + "\\" + userData.getGoogleAuthToken();
        } else {

            folderGeneratorRepository.incrementCount();
            folderIndex = folderGeneratorRepository.getCount();
            createFolderIfNotExist(new File(baseFolderPath + "\\" + folderIndex + "\\" + userData.getGoogleAuthToken()));
            return baseFolderPath + "\\" + folderIndex + "\\" + userData.getGoogleAuthToken();

        }
    }

    private void createFolderIfNotExist(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public FolderGeneratorRepository getFolderGeneratorRepository() {
        return folderGeneratorRepository;
    }

    public void setFolderGeneratorRepository(FolderGeneratorRepository folderGeneratorRepository) {
        this.folderGeneratorRepository = folderGeneratorRepository;
    }

    public String getBaseFolderPath() {
        return baseFolderPath;
    }

    public void setBaseFolderPath(String baseFolderPath) {
        this.baseFolderPath = baseFolderPath;
    }

    public int getMaxFileInFolder() {
        return maxFileInFolder;
    }

    public void setMaxFileInFolder(int maxFileInFolder) {
        this.maxFileInFolder = maxFileInFolder;
    }
}
