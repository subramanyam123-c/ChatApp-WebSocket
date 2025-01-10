package com.dev.ChatApp.Controller;

import com.dev.ChatApp.SessionTracker.WebSocketSessionTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class ActiveUsersController {

    @Autowired
    private WebSocketSessionTracker sessionTracker;

    @GetMapping("/active-users")
    public Set<String> getActiveUsers() {
        return sessionTracker.getActiveUsers();
    }
}
