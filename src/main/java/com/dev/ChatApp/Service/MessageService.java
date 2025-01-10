package com.dev.ChatApp.Service;

import com.dev.ChatApp.Model.Message;
import com.dev.ChatApp.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getMessagesBetweenUsers(String sender, String receiver) {
        // Use the repository to fetch messages with the indexed query
        return messageRepository.findMessagesBetweenUsers(sender, receiver);
    }
    // Additional methods for retrieving messages can be added here
}
