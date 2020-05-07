package com.example.superduperdrive.mapper;

import com.example.superduperdrive.model.Note;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NotesMapper {

    @Select("SELECT * FROM notes")
    List<Note> findAll();

    @Select("SELECT * FROM notes WHERE userid = #{userId}")
    List<Note> findByUserId(Long userId);

    @Select("SELECT * FROM notes WHERE noteid = #{noteId}")
    Note findById(Long id);

    @Insert("INSERT INTO notes (notetitle, notedescription, userid) VALUES (#{note.noteTitle}, #{note.noteDescription}, #{userId})")
    Integer create(@Param("note") Note note, Long userId);

    @Update("UPDATE notes SET notetitle = #{note.noteTitle}, notedescription = #{note.noteDescription} WHERE noteid = #{note.noteId}")
    Integer update(@Param("note") Note note);

    @Delete("DELETE FROM notes WHERE noteid = #{noteId}")
    Integer delete(Long id);
}
