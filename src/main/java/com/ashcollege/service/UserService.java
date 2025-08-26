package com.ashcollege.service;

import com.ashcollege.entities.UserEntity;
import com.ashcollege.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserEntity user) {
        if (userRepository.existsByMail(user.getMail())) {
            throw new RuntimeException("המייל כבר קיים במערכת");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.updatePregnancyDetails();
        userRepository.save(user);
    }

    public UserEntity findByMail(String mail) {
        return userRepository.findByMail(mail);
    }

    public boolean checkPassword(String rawPassword, String storedPassword) {
        return passwordEncoder.matches(rawPassword, storedPassword);
    }

    @Transactional
    public void updateUser(UserEntity user) {
        System.out.println("🔍 UserService.updateUser() נקרא");
        System.out.println("🔍 משתמש ID: " + user.getId());
        System.out.println("🔍 פרטים נוספים לשמירה:");
        System.out.println("  - numberOfBirths: " + user.getNumberOfBirths());
        System.out.println("  - babyGender: " + user.getBabyGender());
        System.out.println("  - preferredHospital: " + user.getPreferredHospital());
        System.out.println("  - healthInsurance: " + user.getHealthInsurance());

        user.updatePregnancyDetails();

        // שמירה עם flush מיידי
        UserEntity saved = userRepository.saveAndFlush(user);
        System.out.println("✅ UserService.updateUser() הושלם, ID: " + saved.getId());
    }

    public UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = (String) auth.getPrincipal();
            return userRepository.findByMail(email);
        }
        return null;
    }
}