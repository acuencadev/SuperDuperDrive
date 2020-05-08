package com.example.superduperdrive.controller;

import com.example.superduperdrive.model.Note;
import com.example.superduperdrive.model.User;
import com.example.superduperdrive.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NoteController {

    private NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping(value = "/add-note")
    public String addNote(Authentication authentication, Note note) {
        User user = (User) authentication.getPrincipal();
        boolean result;

        if (note.getNoteId() == 0) {
            result = noteService.create(note, user.getUserId());
        } else {
            result = noteService.update(note);
        }

        if (!result) {
            return "redirect:/result?error";
        }

        return "redirect:/result?success";
    }

    @GetMapping(value = "/delete-note/{id}")
    public String deleteNote(@PathVariable("id") Long id) {
        boolean result = noteService.delete(id);

        if (!result) {
            return "redirect:/result?error";
        }

        return "redirect:/result?success";
    }
}
