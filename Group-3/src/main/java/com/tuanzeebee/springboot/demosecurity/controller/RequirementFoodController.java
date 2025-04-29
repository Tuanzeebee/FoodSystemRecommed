package com.tuanzeebee.springboot.demosecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RequirementFoodController {

    @GetMapping("/requirement-food")
    public String showRequirementFoodPage() {
        return "requirement-food";
    }

}
