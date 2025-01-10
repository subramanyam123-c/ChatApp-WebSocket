package com.dev.ChatApp.Repository;

import com.dev.ChatApp.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender = :sender AND m.receiver = :receiver) OR " +
            "(m.sender = :receiver AND m.receiver = :sender) " +
            "ORDER BY m.createdAt ASC")
    List<Message> findMessagesBetweenUsers(@Param("sender") String sender, @Param("receiver") String receiver);
}
