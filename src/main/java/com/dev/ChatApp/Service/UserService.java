package com.dev.ChatApp.Service;

import com.dev.ChatApp.Exception.DuplicateEmailException;
import com.dev.ChatApp.JWT.JwtUtil;
import com.dev.ChatApp.Model.LoginDTO;
import com.dev.ChatApp.Model.User;
import com.dev.ChatApp.Model.UserDTO;
import com.dev.ChatApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateEmailException("Email is already in use");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    public String loginUser(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Receiver username not found"));


        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return jwtUtil.generateToken(user.getUsername());
    }

    public User findUser(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Sender username not found"));
    }
    public UUID getUserIdByUsername(String username) {
        return userRepository.findIdByUsername(username);
    }

    public List<User> getAllUsersExcluding(String currentUsername) {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.getUsername().equals(currentUsername))
                .collect(Collectors.toList());
    }

}

