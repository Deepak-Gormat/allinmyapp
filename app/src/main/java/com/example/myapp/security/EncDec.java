package com.example.myapp.security;

import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myapp.userEntery.UserInfoActivity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncDec {
    private static SecretKeySpec secretKeySpec;
    private static byte[] key;

    public static void setKey(String myKey)  {
        MessageDigest sha = null;
        try{
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key,16);
            secretKeySpec = new SecretKeySpec(key,"AES");
        }catch (Exception e){
           String errorMsg = e.getMessage();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static  String encrypt(String strToEncryot, String secretKey){
        try {
            setKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncryot.getBytes("UTF-8")));

        }catch (Exception e){
            String errorMsg = e.getMessage();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String strToDecrypt, String secretKey){
        try {
            setKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));

        }catch (Exception e){
            String errorMsg = e.getMessage();
        }
        return  null;
    }
}
