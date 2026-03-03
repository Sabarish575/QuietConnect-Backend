package com.example.quietconnect_backend.message;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.quietconnect_backend.dto.ChatContentDto;
import com.example.quietconnect_backend.dto.MessageDto;
import com.example.quietconnect_backend.dto.UserDto;
import com.example.quietconnect_backend.user.User;
import com.example.quietconnect_backend.user.UserRepository;

import jakarta.transaction.Transactional;


@Service
public class ChatService {
    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EncryptionService encryptionService;


    @Transactional
    public ResponseEntity<Void> sendMessage(MessageDto message,String email) throws Exception{
        User receiver=userRepo.findById(message.getReceiverId()).orElseThrow();
        User sender=userRepo.findByEmail(email).orElseThrow();
        Message msg=new Message();
        msg.setMessage(encryptionService.encrypt(message.getMessage()));
        msg.setReceiver(receiver);
        msg.setSender(sender);

        simpMessagingTemplate.convertAndSendToUser(receiver.getEmail().toString(), "/queue/messages", message);
        notification(message,sender.getId());
        messageRepo.save(msg);
        return ResponseEntity.ok().build();
    }


    public List<ChatContentDto> getAllMessage(String email,Long r1) throws Exception{
         User sender=userRepo.findByEmail(email).orElseThrow();
        List<ChatContentDto> chatcontent=messageRepo.getChatBetweenUsers(sender.getId(), r1);
        
        if(chatcontent==null || chatcontent.isEmpty()){
            return null;
        }

        for(ChatContentDto dto:chatcontent){
            dto.setMessage(encryptionService.decrypt(dto.getMessage()));
        }

        return chatcontent;
    }

    public void notification(MessageDto message,Long senderId){

        User receiver=userRepo.findById(message.getReceiverId()).orElseThrow();
        simpMessagingTemplate.convertAndSendToUser(
            receiver.getEmail().toLowerCase(),
            "/queue/notifications",
            senderId
        );
    }

    public List<UserDto> getChatFriend(String email){
        User sender=userRepo.findByEmail(email).orElseThrow();
        return messageRepo.getFriend(sender.getId());
    }



}
