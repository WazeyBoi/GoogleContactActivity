package com.paras.googlecontactintegration.controller;

import com.google.api.services.people.v1.model.Person;
import com.paras.googlecontactintegration.service.GooglePeopleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/add-contact")
    public String showAddContactForm() {
        return "addcontacts";
    }

    @GetMapping("/edit-contact")
    public String showEditContactForm(@RequestParam String resourceName, Model model) {
        try {
            Person contact = googlePeopleService.getContact(resourceName);
            if (contact == null) {
                model.addAttribute("error", "Contact not found.");
                return "error";
            }

            // Extract the etag from the first metadata source
            String etag = contact.getMetadata().getSources().get(0).getEtag();

            model.addAttribute("contact", contact);
            model.addAttribute("etag", etag); // Pass etag to the form
            return "editcontacts";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to fetch contact details: " + e.getMessage());
            return "error";
        }
    }

}