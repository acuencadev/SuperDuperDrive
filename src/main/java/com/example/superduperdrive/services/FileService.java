package com.example.superduperdrive.services;

import com.example.superduperdrive.mapper.FileMapper;
import com.example.superduperdrive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileService {

    private FileMapper fileMapper;

    @Autowired
    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public List<File> getAll() {
        return fileMapper.findAll();
    }

    public List<File> getAllByUserId(Long userId) {
        return fileMapper.findByUserId(userId);
    }

    public File getById(Long id) {
        return fileMapper.findById(id);
    }

    public boolean addFile(MultipartFile file, Long userId) {
        try {
            File newFile = new File(file.getOriginalFilename(), file.getContentType(),
                    file.getSize(), file.getBytes());

            fileMapper.create(newFile, userId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFile(Long id) {
        Integer result = fileMapper.delete(id);

        return result > 0;
    }
}
