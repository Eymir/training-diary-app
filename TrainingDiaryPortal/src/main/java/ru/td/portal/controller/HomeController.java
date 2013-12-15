package ru.td.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: Ricoshet
 * Date: 15.12.13
 * Time: 0:58
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping
public class HomeController {

    @RequestMapping({"/","/home"})
    public String showHome(ModelMap model) {

        return "index";
    }
}
