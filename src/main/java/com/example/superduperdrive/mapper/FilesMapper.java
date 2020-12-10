package com.example.superduperdrive.mapper;

import com.example.superduperdrive.model.File;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FilesMapper {

    @Select("SELECT * FROM files")
    List<File> findAll();

    @Select("SELECT * FROM files WHERE userid = #{userId}")
    List<File> findByUserId(Long userId);

    @Select("SELECT * FROM files WHERE fileid = #{id} AND userid = #{userId}")
    File findById(Long id, Long userId);

    @Select("SELECT * FROM files WHERE filename = #{fileName} AND userid = #{userId}")
    File findByNameAndUserId(Long userId, String fileName);

    @Insert("INSERT INTO files (filename, contenttype, filesize, filedata, userid) VALUES (#{file.fileName}, #{file.contentType}, #{file.fileSize}, #{file.fileData}, #{userId})")
    Integer create(@Param("file") File file, Long userId);

    @Update("UPDATE files SET filename = #{file.fileName}, contenttype = #{file.contentType}, filesize = #{file.fileSize}, filedata = #{file.fileData} WHERE fileid = #{file.fileId} AND userid = #{userid}")
    Integer update(@Param("file") File file, Long userId);

    @Delete("DELETE FROM files WHERE fileId = #{id} AND userid = #{userId}")
    Integer delete(Long id, Long userId);
}
