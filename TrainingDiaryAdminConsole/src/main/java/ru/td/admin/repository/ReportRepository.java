package ru.td.admin.repository;

import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.td.admin.service.LogFinder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

/**
 * User: Vladimir Koba
 * Date: 23.03.14
 * Time: 0:40
 */
public class ReportRepository {
    LogFinder logFinderService;
    JdbcTemplate jdbcTemplate;

    public String getLogFileInfo(int countOfLines) {
        try {
            File logFile = logFinderService.getLogFile();
            checkForNull(logFile);
            List<String> logLines = IOUtils.readLines(new BufferedInputStream(new FileInputStream(logFile)));
            Collections.reverse(logLines);
            StringBuilder result = new StringBuilder();
            if (logLines.size() < countOfLines) {
                countOfLines = logLines.size();
            }
            for (int i = countOfLines - 1; i >= 0; i--) {
                result.append(logLines.get(i) + "\n");
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("Processing log file is failed! Details:", e);
        }
    }

    private void checkForNull(File logFile) {
        if (logFile == null) {
            throw new RuntimeException("Log file is null!");
        }
    }

    public long getNumberOfUsers() {
        return 0;
//        String sql = "";
//        return jdbcTemplate.queryForInt(sql);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LogFinder getLogFinderService() {
        return logFinderService;
    }

    public void setLogFinderService(LogFinder logFinderService) {
        this.logFinderService = logFinderService;
    }
}
