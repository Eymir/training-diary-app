package ru.td.admin.service;

import java.io.File;

/**
 * User: Vladimir Koba
 * Date: 23.03.14
 * Time: 1:33
 */
public class FileLogFinder implements LogFinder {
    String path;

    @Override
    public File getLogFile() {
        return new File(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
