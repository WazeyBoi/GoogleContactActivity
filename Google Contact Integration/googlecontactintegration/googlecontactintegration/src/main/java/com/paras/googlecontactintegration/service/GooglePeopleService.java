package com.paras.googlecontactintegration.service;

import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GooglePeopleService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public GooglePeopleService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    private String getAccessToken() {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );
            if (client != null) {
                String token = client.getAccessToken().getTokenValue();
                System.out.println("OAuth2 Access Token: " + token); // DEBUGGING TOKEN
                return token;
            }
        }
        throw new RuntimeException("OAuth2 authentication failed!");
    }

    private PeopleService createPeopleService() {
        return new PeopleService.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                new com.google.api.client.json.gson.GsonFactory(),
                request -> request.getHeaders().setAuthorization("Bearer " + getAccessToken())
        ).setApplicationName("Google Contacts App").build();
    }

    public List<Person> getContacts() throws IOException {
        try {
            PeopleService peopleService = createPeopleService();
            ListConnectionsResponse response = peopleService.people().connections()
                    .list("people/me")
                    .setPersonFields("names,emailAddresses")
                    .execute();

            List<Person> contacts = response.getConnections() != null ? response.getConnections() : new ArrayList<>();
            System.out.println("Fetched Contacts Count: " + contacts.size()); // DEBUGGING CONTACT COUNT
            return contacts;

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error fetching contacts: " + e.getMessage());
            throw new IOException("Failed to retrieve contacts from Google People API", e);
        }
    }

    public Person addContact(String name, String email) throws IOException {
        PeopleService peopleService = createPeopleService();

        Person newContact = new Person()
                .setNames(List.of(new Name().setGivenName(name)))
                .setEmailAddresses(List.of(new EmailAddress().setValue(email)));

        Person createdContact = peopleService.people().createContact(newContact).execute();
        System.out.println("Created Contact: " + createdContact.getResourceName());
        return createdContact;
    }

    public Person updateContact(String resourceName, String newName, String newEmail) throws IOException {
        PeopleService peopleService = createPeopleService();

        Person updatedContact = new Person()
                .setNames(List.of(new Name().setGivenName(newName)))
                .setEmailAddresses(List.of(new EmailAddress().setValue(newEmail)));

        Person response = peopleService.people().updateContact(resourceName, updatedContact)
                .setUpdatePersonFields("names,emailAddresses")
                .execute();

        System.out.println("Updated Contact: " + response.getResourceName());
        return response;
    }

    public void deleteContact(String resourceName) throws IOException {
        PeopleService peopleService = createPeopleService();
        peopleService.people().deleteContact(resourceName).execute();
        System.out.println("Deleted Contact: " + resourceName);
    }

}
