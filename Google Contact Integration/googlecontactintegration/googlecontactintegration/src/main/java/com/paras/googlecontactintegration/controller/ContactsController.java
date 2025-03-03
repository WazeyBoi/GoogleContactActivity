package com.paras.googlecontactintegration.controller;

import com.google.api.services.people.v1.model.Person;
import com.paras.googlecontactintegration.service.GooglePeopleService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/contacts")
public class ContactsController {

    private final GooglePeopleService googlePeopleService;

    public ContactsController(GooglePeopleService googlePeopleService) {
        this.googlePeopleService = googlePeopleService;
    }

    @GetMapping
    @ResponseBody
    public List<Person> getContacts() throws IOException {
        List<Person> contacts = googlePeopleService.getContacts();
        System.out.println("Fetched Contacts: " + contacts);
        return contacts;
    }

    @PostMapping("/add")
    public String addContact(@RequestParam String firstName, @RequestParam String lastName,
                            @RequestParam String email, @RequestParam String phoneNumber,
                            RedirectAttributes redirectAttributes) {
        try {
            googlePeopleService.addContact(firstName, lastName, email, phoneNumber);
            redirectAttributes.addFlashAttribute("message", "Contact added successfully!");
            return "redirect:/contacts";
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add contact.");
            return "redirect:/contacts";
        }
    }

    @PostMapping("/update")
    public String updateContact(@RequestParam String resourceName,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                @RequestParam String phoneNumber,
                                RedirectAttributes redirectAttributes) {
        try {
            googlePeopleService.updateContact(resourceName, firstName, lastName, email, phoneNumber);
            redirectAttributes.addFlashAttribute("message", "Contact updated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getMessage().contains("etag")) {
                redirectAttributes.addFlashAttribute("error", "Contact was modified by someone else. Please reload and try again.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update contact: " + e.getMessage());
            }
        }
        return "redirect:/contacts";
    }

    @PostMapping("/delete")
    public String deleteContact(@RequestParam String resourceName, RedirectAttributes redirectAttributes) {
        try {
            googlePeopleService.deleteContact(resourceName);
            redirectAttributes.addFlashAttribute("message", "Contact deleted successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to delete contact.");
        }
        return "redirect:/contacts";
    }

}