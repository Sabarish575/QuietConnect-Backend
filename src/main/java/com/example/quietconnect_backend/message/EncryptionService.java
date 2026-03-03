package com.example.quietconnect_backend.message;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.quietconnect_backend.Encryption.AESUtil;

import jakarta.annotation.PostConstruct;

@Service
public class EncryptionService {

    @Value("${chat.encryption.key}")
    private String base64keyString;
    
    private SecretKey secretKey;

    @PostConstruct
    public void init(){

        try {
            byte[] decodeKey=Base64.getDecoder().decode(base64keyString);
            this.secretKey=new SecretKeySpec(decodeKey,0,decodeKey.length,"AES");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize encryption key",e);
        }


    }

    public String encrypt(String data) throws Exception{
       return AESUtil.encrypt(data, secretKey);
    }

    public String decrypt(String data) throws Exception{
        try {
            return AESUtil.decrypt(data, secretKey);
        } catch (Exception e) {
            return "message encrypted with old key";
        }
    }

}
