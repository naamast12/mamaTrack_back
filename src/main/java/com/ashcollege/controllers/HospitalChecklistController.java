package com.ashcollege.controllers;

import com.ashcollege.entities.HospitalChecklistEntity;
import com.ashcollege.service.HospitalChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hospital-checklist")
public class HospitalChecklistController {
    private final HospitalChecklistService hospitalChecklistService;

    @Autowired
    public HospitalChecklistController(HospitalChecklistService hospitalChecklistService) {
        this.hospitalChecklistService = hospitalChecklistService;
    }

    // 🔧 בדיקת תקינות של הקונטרולר
    @GetMapping("/test")
    public String test() {
        System.out.println("🎯 Hospital Checklist controller loaded");
        return "Hospital Checklist controller is working!";
    }

    // 🔄 שמירה
    @PostMapping
    public ResponseEntity<?> saveChecklist(@RequestBody Map<String, Object> body) {
        try {
            String itemsStatusJson = body.get("itemsStatus").toString();
            HospitalChecklistEntity saved = hospitalChecklistService.saveChecklist(itemsStatusJson);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "רשימת ציוד חדר לידה נשמרה בהצלחה",
                    "checklist", saved
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "שגיאה בשמירת רשימת ציוד חדר לידה: " + e.getMessage()
            ));
        }
    }

    // 📥 קבלת הרשימה למשתמש הנוכחי
    @GetMapping
    public ResponseEntity<?> getChecklist() {
        try {
            HospitalChecklistEntity checklist = hospitalChecklistService.getChecklistForCurrentUser();

            if (checklist == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "לא נמצאה רשימת ציוד חדר לידה למשתמש זה"
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "checklist", checklist
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "שגיאה בקבלת רשימת ציוד חדר לידה: " + e.getMessage()
            ));
        }
    }

    // ♻️ איפוס הרשימה
    @PostMapping("/reset")
    public ResponseEntity<?> resetChecklist() {
        try {
            HospitalChecklistEntity reset = hospitalChecklistService.resetChecklist();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "רשימת ציוד חדר לידה אופסה בהצלחה",
                    "checklist", reset
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "שגיאה באיפוס רשימת ציוד חדר לידה: " + e.getMessage()
            ));
        }
    }

    // 🗑️ מחיקת הרשימה
    @DeleteMapping
    public ResponseEntity<?> deleteChecklist() {
        try {
            hospitalChecklistService.deleteChecklistForCurrentUser();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "רשימת ציוד חדר לידה נמחקה בהצלחה"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "שגיאה במחיקת רשימת ציוד חדר לידה: " + e.getMessage()
            ));
        }
    }
} 