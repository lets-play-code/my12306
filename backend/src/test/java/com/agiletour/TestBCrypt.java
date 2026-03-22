package com.agiletour;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.junit.jupiter.api.Test;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36ZfPIlwiNJDt0e2p6qGmQe";
        System.out.println("Testing hash: " + hash);
        System.out.println("password matches: " + encoder.matches("password", hash));
        System.out.println("password123 matches: " + encoder.matches("password123", hash));
        
        // Generate new hash for "password"
        String newHash = encoder.encode("password");
        System.out.println("New hash for 'password': " + newHash);
        System.out.println("New hash matches: " + encoder.matches("password", newHash));
    }
    
    @Test
    void dummy() {
        main(new String[]{});
    }
}
