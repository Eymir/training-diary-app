package ru.vkoba.ts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.vkoba.ts.service.ReportService;

/**
 * Created with IntelliJ IDEA.
 * User: Ricoshet
 * Date: 02.12.13
 * Time: 23:15
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/Statistic")
public class StatisticController {
    @Autowired
    ReportService reportService;

    @RequestMapping(method = RequestMethod.GET)
    public String getStatistic(ModelMap model) {
        model.addAttribute("deviceCountMap", reportService.getDeviceIdCountMap());
        model.addAttribute("total", reportService.getTotal());
        return "statistic";
    }

    public ReportService getReportService() {
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
