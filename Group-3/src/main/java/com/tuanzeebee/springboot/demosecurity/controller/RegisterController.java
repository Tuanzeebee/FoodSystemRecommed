package com.tuanzeebee.springboot.demosecurity.controller;

import com.tuanzeebee.springboot.demosecurity.entity.User;
import com.tuanzeebee.springboot.demosecurity.service.UserService;
import com.tuanzeebee.springboot.demosecurity.user.WebUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@Controller
@RequestMapping("/register")
public class RegisterController {
    //note
    private Logger logger = Logger.getLogger(getClass().getName());

    private UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showRegistrationForm")
    public String showMyLoginPage(Model model) {

        if (!model.containsAttribute("webUser")) {
            model.addAttribute("webUser", new WebUser());
        }
        return "register/registerform";
    }

    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("webUser") WebUser theWebUser,
            BindingResult theBindingResult,
            RedirectAttributes redirectAttributes) {

        String userName = theWebUser.getUserName();
        logger.info("Processing registration form for: " + userName);
        if (theBindingResult.hasErrors()){
            logger.warning("Validation errors found: " + theBindingResult.toString());

            redirectAttributes.addFlashAttribute("webUser", theWebUser);
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "webUser", theBindingResult);

            return "redirect:/register/showRegistrationForm";
        }

        User existing = userService.findByUserName(userName);
        if (existing != null){
            logger.warning("User name already exists.");

            redirectAttributes.addFlashAttribute("webUser", theWebUser);
            redirectAttributes.addFlashAttribute("registrationError", "User name already exists.");

            return "redirect:/register/showRegistrationForm";
        }

        userService.save(theWebUser);
        logger.info("Successfully created user: " + userName);

        redirectAttributes.addFlashAttribute("registrationSuccess", "Account created successfully!");

        return "redirect:/register/registrationConfirmation";
    }

    @GetMapping("/registrationConfirmation")
    public String showRegistrationConfirmation() {

        return "register/registration-confirmation";
    }
}