package com.example.superduperdrive.services;

import com.example.superduperdrive.mapper.CredentialMapper;
import com.example.superduperdrive.model.Credential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class CredentialService {

    private CredentialMapper credentialMapper;
    private EncryptionService encryptionService;

    @Autowired
    public CredentialService(CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public List<Credential> getAll() {
        List<Credential> credentials = credentialMapper.findAll();

        if (credentials == null) {
            return new ArrayList<>();
        }

        for (Credential credential : credentials) {
            credential.setDecryptedPassword(decryptPassword(credential.getPassword(),
                    credential.getKey()));
        }

        return credentials;
    }

    public List<Credential> getAllByUserId(Long userId) {
        List<Credential> credentials = credentialMapper.findByUserId(userId);

        if (credentials == null) {
            return new ArrayList<>();
        }

        for (Credential credential : credentials) {
            credential.setDecryptedPassword(decryptPassword(credential.getPassword(),
                    credential.getKey()));
        }

        return credentials;
    }

    public Credential getById(Long id) {
        Credential credential = credentialMapper.findById(id);

        if (credential != null) {
            credential.setDecryptedPassword(decryptPassword(credential.getPassword(),
                    credential.getKey()));
        }

        return credential;
    }

    public boolean create(Credential credential, Long userId) {
        Integer result = credentialMapper.create(encryptPassword(credential), userId);

        return result > 0;
    }

    public boolean update(Credential credential, Long userId) {
        Integer result = credentialMapper.update(encryptPassword(credential), userId);

        return result > 0;
    }

    public boolean delete(Long id, Long userId) {
        Integer result = credentialMapper.delete(id, userId);

        return result > 0;
    }

    private Credential encryptPassword(Credential credential) {
        if (credential.getKey() == null) {
            SecureRandom random = new SecureRandom();
            byte[] key = new byte[16];
            random.nextBytes(key);
            credential.setKey(Base64.getEncoder().encodeToString(key));
        }

        String encryptedPassword = encryptionService.encryptValue(credential.getPassword(), credential.getKey());

        credential.setPassword(encryptedPassword);

        return credential;
    }

    private String decryptPassword(String password, String key) {
        return encryptionService.decryptValue(password, key);
    }
}
