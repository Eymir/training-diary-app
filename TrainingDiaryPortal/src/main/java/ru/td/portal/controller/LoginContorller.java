package ru.td.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * Created with IntelliJ IDEA.
 * User: vkoba_000
 * Date: 12/17/13
 * Time: 5:34 PM
 * To change this template use File | Settings | File Templates.
 */

@Controller
public class LoginContorller {

    @RequestMapping({"/login"})
    public String showHome(ModelMap model) {

        return "index";
    }

    @RequestMapping({"/loginfailed"})
    public String showLoginFailure(ModelMap model) {
        model.addAttribute("error", "true");
        return "index";
    }

    @RequestMapping({"/main"})
    public String showMain(ModelMap model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "main";
    }

    @RequestMapping({"/logout"})
    public String showLogout(ModelMap model) {
        return "index";
    }
}
