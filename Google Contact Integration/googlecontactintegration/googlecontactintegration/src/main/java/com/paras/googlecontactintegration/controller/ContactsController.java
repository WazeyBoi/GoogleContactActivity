package com.paras.googlecontactintegration.controller;

import com.google.api.services.people.v1.model.Person;
import com.paras.googlecontactintegration.service.GooglePeopleService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactsController {

    private final GooglePeopleService googlePeopleService;

    public ContactsController(GooglePeopleService googlePeopleService) {
        this.googlePeopleService = googlePeopleService;
    }

    @GetMapping
    public List<Person> getContacts() throws IOException {
        List<Person> contacts = googlePeopleService.getContacts();
        System.out.println("Fetched Contacts: " + contacts);
        return contacts;
    }

    @PostMapping("/api/contacts/add")
    public Person addNewContact(@RequestParam String name, @RequestParam String email) throws IOException {
        return googlePeopleService.addContact(name, email);
    }

    @PutMapping("/api/contacts/update")
    public Person updateExistingContact(@RequestParam String resourceName,
                                        @RequestParam String name,
                                        @RequestParam String email) throws IOException {
        return googlePeopleService.updateContact(resourceName, name, email);
    }

    @DeleteMapping("/api/contacts/delete")
    public String deleteExistingContact(@RequestParam String resourceName) throws IOException {
        googlePeopleService.deleteContact(resourceName);
        return "Deleted: " + resourceName;
    }

}