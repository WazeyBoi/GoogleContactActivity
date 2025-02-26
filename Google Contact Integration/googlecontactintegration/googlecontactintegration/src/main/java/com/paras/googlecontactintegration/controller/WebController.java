package com.paras.googlecontactintegration.controller;

import com.google.api.services.people.v1.model.Person;
import com.paras.googlecontactintegration.service.GooglePeopleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;

@Controller // This ensures it returns Thymeleaf templates
public class WebController {

    private final GooglePeopleService googlePeopleService;

    public WebController(GooglePeopleService googlePeopleService) {
        this.googlePeopleService = googlePeopleService;
    }

    @GetMapping("/contacts")
    public String showContacts(Model model) {
        try {
            List<Person> contacts = googlePeopleService.getContacts();
            model.addAttribute("contacts", contacts);
            return "contacts"; // Must have contacts.html in /templates
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to fetch contacts.");
            return "error"; // Must have error.html to handle failures
        }
    }
}