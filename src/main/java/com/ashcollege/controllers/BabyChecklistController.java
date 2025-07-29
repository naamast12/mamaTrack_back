package com.ashcollege.controllers;

import com.ashcollege.entities.BabyChecklistEntity;
import com.ashcollege.service.BabyChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/baby-checklist")
public class BabyChecklistController {
    private final BabyChecklistService babyChecklistService;

    @Autowired
    public BabyChecklistController(BabyChecklistService babyChecklistService) {
        this.babyChecklistService = babyChecklistService;
    }

    // 🔧 בדיקת תקינות של הקונטרולר
    @GetMapping("/test")
    public String test() {
        System.out.println("🎯 Checklist controller loaded");
        return "Checklist controller is working!";
    }

    // 🔄 שמירה
    @PostMapping
    public ResponseEntity<?> saveChecklist(@RequestBody Map<String, Object> body) {
        try {
            String itemsStatusJson = body.get("itemsStatus").toString();
            BabyChecklistEntity saved = babyChecklistService.saveChecklist(itemsStatusJson);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "רשימת הציוד נשמרה בהצלחה",
                    "checklist", saved
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "שגיאה בשמירת רשימת הציוד: " + e.getMessage()
            ));
        }
    }

    // 📥 קבלת הרשימה למשתמש הנוכחי
    @GetMapping
    public ResponseEntity<?> getChecklist() {
        try {
            BabyChecklistEntity checklist = babyChecklistService.getChecklistForCurrentUser();

            if (checklist == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "לא נמצאה רשימת ציוד למשתמש זה"
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "checklist", checklist
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "שגיאה בקבלת רשימת הציוד: " + e.getMessage()
            ));
        }
    }

    // ♻️ איפוס הרשימה
    @PostMapping("/reset")
    public ResponseEntity<?> resetChecklist() {
        try {
            BabyChecklistEntity reset = babyChecklistService.resetChecklist();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "רשימת הציוד אופסה בהצלחה",
                    "checklist", reset
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "שגיאה באיפוס רשימת הציוד: " + e.getMessage()
            ));
        }
    }
}
