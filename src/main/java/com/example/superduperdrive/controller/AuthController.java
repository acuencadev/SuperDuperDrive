package com.example.superduperdrive.controller;

import com.example.superduperdrive.model.User;
import com.example.superduperdrive.services.UsernameAlreadyExistException;
import com.example.superduperdrive.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/signup")
    public String signUp(Model model,
                         @RequestParam("error")Optional<String> error,
                         @RequestParam("errorType")Optional<String> errorType) {
        if (error.isPresent() && errorType.isPresent()) {
            model.addAttribute("error", true);

            if (errorType.get().equals("generic")) {
                model.addAttribute("errorMessage", "There was an error during the user creation. Try again later or contact support.");
            } else {
                model.addAttribute("errorMessage", "The username already exists. Try with a different one.");
            }
        }

        return "signup";
    }

    @PostMapping(value = "/register")
    public String register(@ModelAttribute("SpringWeb")User user) {
        if (user == null) {
            return "redirect:signup";
        }

        try {
            userService.register(user);
        } catch (UsernameAlreadyExistException ue) {
            return "redirect:signup?error&errorType=alreadyexists";
        } catch (Exception e) {
            return "redirect:signup?error&errorType=generic";
        }

        return "redirect:signup?success";
    }
}
