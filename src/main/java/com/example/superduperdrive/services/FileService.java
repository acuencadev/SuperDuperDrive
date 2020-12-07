package com.example.superduperdrive.services;

import com.example.superduperdrive.mapper.FilesMapper;
import com.example.superduperdrive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileService {

    private FilesMapper fileMapper;

    @Autowired
    public FileService(FilesMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public List<File> getAll() {
        return fileMapper.findAll();
    }

    public List<File> getAllByUserId(Long userId) {
        return fileMapper.findByUserId(userId);
    }

    public File getById(Long id, Long userId) {
        return fileMapper.findById(id, userId);
    }

    public FileOperationResponse addFile(MultipartFile file, Long userId) {
        try {
            if (fileMapper.findByNameAndUserId(userId, file.getOriginalFilename()) != null)
            {
                return FileOperationResponse.FileExists;
            }

            File newFile = new File(file.getOriginalFilename(), file.getContentType(),
                    file.getSize(), file.getBytes());

            fileMapper.create(newFile, userId);

            return FileOperationResponse.FileAdded;
        } catch (Exception e) {
            e.printStackTrace();
            return FileOperationResponse.FileNotCreated;
        }
    }

    public FileOperationResponse deleteFile(Long id, Long userId) {
        Integer result = fileMapper.delete(id, userId);

        return (result > 0) ? FileOperationResponse.FileDeleted : FileOperationResponse.FileNotDeleted;
    }
}
