package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        
        User user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");
        
        userRepository.save(user1);
        userRepository.save(user2);
    }
}
