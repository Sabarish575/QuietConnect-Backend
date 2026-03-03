package com.example.quietconnect_backend.Encryption;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class AESUtil {

    private String input;

    AESUtil(String data){
        this.input=data;
    }

    //need algo,gcm length,iv length
    private static final String algo="AES/GCM/NoPadding";
    private static final int iv_len=12;
    private static final int gcm_len=128;

    //key generation ✅
    public static SecretKey generateKey() throws NoSuchAlgorithmException{
        KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static String encrypt(String data,SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{

        //initialisation vector for randomness ✅
        byte[] iv=new byte[iv_len];
        new SecureRandom().nextBytes(iv);

        

        //algo setup and setted to encryption mode✅
        Cipher ciper=Cipher.getInstance(algo);
        ciper.init(Cipher.ENCRYPT_MODE,key,new GCMParameterSpec(gcm_len, iv));
        byte[] cipherText=ciper.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        //byte buffer for better performance and searchability ✅
        ByteBuffer bf=ByteBuffer.allocate(iv.length+cipherText.length);
        bf.put(iv);
        bf.put(cipherText);

        //return as string✅
        return Base64.getEncoder().encodeToString(bf.array());
    }

    public static String decrypt(String data,SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{

        //whole process is reverse the string into bytes so it becomes
        // [iv][cipher text] apply in decrypt mode and get the answer

        //convert the string into byte
        byte[] decoded=Base64.getDecoder().decode(data);

        //gives that byte to bytebuffer for better traversal and finding the required elements
        ByteBuffer bf=ByteBuffer.wrap(decoded);

        //since the format is [iv][cipher text]
        //first we fetch the iv by defining the size of it that is 12 bytes
        byte[] iv=new byte[iv_len];
        bf.get(iv);

        //then the cipher text since the length is unknown and 
        // format is fixed after fetching iv we can use the remaining()
        byte[] cipherText=new byte[bf.remaining()];
        bf.get(cipherText);

        //initialize the decryption ritual
        //setting the algo and set the decrypt mode on
        Cipher cipher=Cipher.getInstance(algo);
        cipher.init(Cipher.DECRYPT_MODE,key,new GCMParameterSpec(gcm_len, iv));

        return new String(cipher.doFinal(cipherText),StandardCharsets.UTF_8);
    }





    
}
