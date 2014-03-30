package ru.td.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.td.admin.repository.ReportRepository;

import java.security.Principal;

/**
 * User: Vladimir Koba
 * Date: 16.03.14
 * Time: 1:04
 */

@Controller
public class AdminController {
    ReportRepository reportRepository;

    @RequestMapping({"/", "/login"})
    public String showHome(ModelMap model) {
        return "index";
    }

    @RequestMapping({"/loginfailed"})
    public String showLoginFailure(ModelMap model) {
        model.addAttribute("error", "true");
        return "index";
    }

    @RequestMapping({"/main"})
    public String showMain(ModelMap model, Principal principal, Integer countOfLines) {
        countOfLines = checkCountOfLines(countOfLines);
        model.addAttribute("username", principal.getName());
        model.addAttribute("logInfo", reportRepository.getLogFileInfo(countOfLines));
        model.addAttribute("numberOfUsers", reportRepository.getNumberOfUsers());

        model.addAttribute("countOfLines", countOfLines);
        return "main";
    }

    private Integer checkCountOfLines(Integer countOfLines) {
        if (countOfLines == null || countOfLines == 0) {
            countOfLines = 10; //дефолтное значение
        }
        return countOfLines;
    }

    @RequestMapping({"/logout"})
    public String showLogout(ModelMap model) {
        return "index";
    }

    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
}
