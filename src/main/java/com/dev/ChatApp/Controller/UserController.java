package com.dev.ChatApp.Controller;

import com.dev.ChatApp.Model.LoginDTO;
import com.dev.ChatApp.Model.UserDTO;
import com.dev.ChatApp.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow all origins

public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDTO userDTO) {
        userService.createUser(userDTO);
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = userService.loginUser(loginDTO);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

}


