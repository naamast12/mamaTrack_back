package com.ashcollege.service;

import com.ashcollege.entities.UserEntity;
import com.ashcollege.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(UserEntity user) {
        // בדיקה אם המייל כבר קיים
        if (userRepository.existsByMail(user.getMail())) {
            throw new RuntimeException("המייל כבר קיים במערכת");
        }

        user.updatePregnancyDetails(); // מחשב את תאריך הלידה ושבוע ההריון
        userRepository.save(user);
    }

    public UserEntity findByMail(String mail) {
        return userRepository.findByMail(mail);
    }

    public boolean checkPassword(String rawPassword, String storedPassword) {
        return rawPassword.equals(storedPassword);
    }

    public void updateUser(UserEntity user) {
        user.updatePregnancyDetails(); // מחשב מחדש אם תאריך הווסת השתנה
        userRepository.save(user);
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
