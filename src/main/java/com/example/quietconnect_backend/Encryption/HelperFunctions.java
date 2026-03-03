package com.example.quietconnect_backend.Encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class HelperFunctions {

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException{
        KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key=keyGenerator.generateKey();
        return key;
    }

    public static GCMParameterSpec generateIv(){
        byte[] iv=new byte[12];  //iv typically 12 bytes for performance and security;
        new SecureRandom().nextBytes(iv);
        return new GCMParameterSpec(128, iv);
    }

    public static String encrypt(String algo,String input, SecretKey key,GCMParameterSpec iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{

        Cipher cipher=Cipher.getInstance(algo);
        cipher.init(Cipher.ENCRYPT_MODE, key,iv);
        byte[] cipherText=cipher.doFinal(input.getBytes());
        System.out.println("byte g "+Arrays.toString(cipherText));
        System.out.println("cool your cipher g "+cipherText);
        return Base64.getEncoder().encodeToString(cipherText);
    }
    public static String decrypt(String algo,String cipherText, SecretKey key,GCMParameterSpec iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{

        Cipher cipher=Cipher.getInstance(algo);
        cipher.init(Cipher.DECRYPT_MODE, key,iv);
        byte[] plainText=cipher.doFinal(
            Base64.getDecoder().decode(cipherText));
        System.out.println("cool your plain g "+plainText);
        return new String(plainText);
    }

}