package com.example.superduperdrive.controller;

import com.example.superduperdrive.model.User;
import com.example.superduperdrive.services.FileService;
import com.example.superduperdrive.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private NoteService noteService;
    private FileService fileService;

    @Autowired
    public HomeController(NoteService noteService, FileService fileService) {
        this.noteService = noteService;
        this.fileService = fileService;
    }

    @GetMapping(value = {"/", "/dashboard"})
    public ModelAndView showIndex(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        ModelAndView modelAndView = new ModelAndView("home");

        modelAndView.addObject("notes", noteService.getAllByUserId(user.getUserId()));
        modelAndView.addObject("files", fileService.getAllByUserId(user.getUserId()));

        return modelAndView;
    }
}
