package com.example.superduperdrive.services;

import com.example.superduperdrive.mapper.NotesMapper;
import com.example.superduperdrive.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private NotesMapper notesMapper;

    @Autowired
    public NoteService(NotesMapper notesMapper) {
        this.notesMapper = notesMapper;
    }

    public List<Note> getAll() {
        return notesMapper.findAll();
    }

    public List<Note> getAllByUserId(Long userId) {
        return notesMapper.findByUserId(userId);
    }

    public Note getById(Long id) {
        return notesMapper.findById(id);
    }

    public boolean create(Note note, Long userId) {
        return notesMapper.create(note, userId) > 0;
    }

    public boolean update(Note note, Long userId) {
        return notesMapper.update(note, userId) > 0;
    }

    public boolean delete(Long id, Long userId) {
        return notesMapper.delete(id, userId) > 0;
    }
}
