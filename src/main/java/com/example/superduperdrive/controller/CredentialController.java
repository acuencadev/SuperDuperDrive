package com.example.superduperdrive.controller;

import com.example.superduperdrive.model.Credential;
import com.example.superduperdrive.model.User;
import com.example.superduperdrive.services.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CredentialController {

    private CredentialService credentialService;

    @Autowired
    public CredentialController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @PostMapping(value = "/add-credential")
    public String addCredential(Authentication authentication, Credential credential) {
        User user = (User) authentication.getPrincipal();
        boolean result = false;

        if (credential.getCredentialId() == 0) {
            result = credentialService.create(credential, user.getUserId());
        } else {
            result = credentialService.update(credential);
        }

        if (!result) {
            return "redirect:/result?error";
        }

        return "redirect:/result?success";
    }

    @GetMapping(value = "/delete-credential/{id}")
    public String deleteCredential(@PathVariable("id") Long id) {
        boolean result = credentialService.delete(id);

        if (!result) {
            return "redirect:/result?error";
        }

        return "redirect:/result?success";
    }
}
