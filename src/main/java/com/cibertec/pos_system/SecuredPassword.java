package com.cibertec.pos_system;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecuredPassword {
    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // Utilizamos BCryptPasswordEncoder para encriptar el password
        String rawPassword = "password"; //
        String encodedPassword = encoder.encode(rawPassword); // Encriptamos passworrd

      System.out.println(encodedPassword);

    }
}
