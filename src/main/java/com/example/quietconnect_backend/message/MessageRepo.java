package com.example.quietconnect_backend.message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quietconnect_backend.dto.ChatContentDto;
import com.example.quietconnect_backend.dto.UserDto;

@Repository
public interface MessageRepo extends JpaRepository<Message,Long> {


        @Query("""
        SELECT new com.example.quietconnect_backend.dto.ChatContentDto(
            m.sender.id,
            m.receiver.id,
            m.message,
            m.createdAt
        )
        FROM Message m
        WHERE 
        (m.sender.id = :senderId AND m.receiver.id = :receiverId)
        OR (m.sender.id = :receiverId AND m.receiver.id = :senderId)
        ORDER BY m.createdAt ASC
        """)
        List<ChatContentDto> getChatBetweenUsers(
                @Param("senderId") Long senderId,
                @Param("receiverId") Long receiverId
        );

 @Query("""
SELECT new com.example.quietconnect_backend.dto.UserDto(
    CASE 
        WHEN m.sender.id = :userId THEN m.receiver.id 
        ELSE m.sender.id 
    END,
    CASE 
        WHEN m.sender.id = :userId THEN m.receiver.username 
        ELSE m.sender.username 
    END,
    CASE 
        WHEN m.sender.id = :userId THEN m.receiver.bio 
        ELSE m.sender.bio 
    END
)
FROM Message m
WHERE m.sender.id = :userId OR m.receiver.id = :userId
GROUP BY
    CASE 
        WHEN m.sender.id = :userId THEN m.receiver.id 
        ELSE m.sender.id 
    END,
    CASE 
        WHEN m.sender.id = :userId THEN m.receiver.username 
        ELSE m.sender.username 
    END,
    CASE 
        WHEN m.sender.id = :userId THEN m.receiver.bio 
        ELSE m.sender.bio 
    END
ORDER BY MAX(m.createdAt) DESC
""")
List<UserDto> getFriend(Long userId);

    
}
