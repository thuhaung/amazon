package com.example.amazon.Util;

import java.util.Random;

public class TokenGenerationUtil {

    public static String generateTokenForEmail() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();

        while (salt.length() < 6) {
            int index = (int) (random.nextFloat() * chars.length());
            salt.append(chars.charAt(index));
        }

        return salt.toString();
    }
}
