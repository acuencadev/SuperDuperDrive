package com.example.superduperdrive.services;

import com.example.superduperdrive.mapper.UsersMapper;
import com.example.superduperdrive.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private UsersMapper userMapper;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UsersMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User getByUserId(Long userId) throws UsernameNotFoundException {
        User user = userMapper.findById(userId);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("The user with ID '%s' was not found.", userId));
        }

        return user;
    }

    public User register(User user) throws UsernameAlreadyExistException {
        User existingUser = userMapper.findByUsername(user.getUsername());

        if (existingUser != null)
        {
            throw new UsernameAlreadyExistException("Username already exists.");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(hashedPassword);

        try {
            Integer i = userMapper.create(user);

            System.out.println(i);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("The user '%s' was not found.", username));
        }

        return user;
    }
}
