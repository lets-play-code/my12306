package com.agiletour;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {
    @Test
    void testBCrypt() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password";
        String hash = encoder.encode(password);
        System.out.println("Generated hash: " + hash);
        
        // Verify the hash matches
        assertTrue(encoder.matches(password, hash), "Hash should match original password");
        
        // Test against known hash from database
        String dbHash = "$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36ZfPIlwiNJDt0e2p6qGmQe";
        System.out.println("Testing 'password' against DB hash: " + encoder.matches("password", dbHash));
        System.out.println("Testing 'password123' against DB hash: " + encoder.matches("password123", dbHash));
    }
}
