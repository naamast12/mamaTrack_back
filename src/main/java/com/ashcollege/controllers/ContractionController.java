package com.ashcollege.controllers;

import com.ashcollege.entities.UserEntity;
import com.ashcollege.responses.ContractionDto;
import com.ashcollege.service.ContractionService;
import com.ashcollege.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contractions")
public class ContractionController {

    private final ContractionService contractionService;
    private final UserService userService;


    @Autowired
    public ContractionController(ContractionService contractionService, UserService userService) {
        this.contractionService = contractionService;
        this.userService = userService;
    }

    // POST: שמירת ציר
    @PostMapping
    public ResponseEntity<?> saveContraction(@RequestBody ContractionDto dto) {
        try {
            String mail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserEntity user = userService.findByMail(mail);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "User not found"
                ));
            }

            dto.setUserId(user.getId());
            ContractionDto saved = contractionService.saveContraction(dto);
            boolean shouldGo = contractionService.shouldGoToHospital(user.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Contraction saved successfully",
                    "contraction", saved,
                    "shouldGoToHospital", shouldGo
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Failed to save contraction: " + e.getMessage()
            ));
        }
    }

    // GET: קבלת הצירים של המשתמש המחובר
    @GetMapping
    public ResponseEntity<?> getContractionsForCurrentUser() {
        try {
            String mail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserEntity user = userService.findByMail(mail);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "User not found"
                ));
            }

            List<ContractionDto> contractions = contractionService.getContractionsByUserId(user.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "contractions", contractions
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error retrieving contractions: " + e.getMessage()
            ));
        }
    }

    // DELETE: מחיקת כל הצירים של המשתמש המחובר
    @DeleteMapping
    public ResponseEntity<?> deleteContractionsForCurrentUser() {
        try {
            // ✅ שליפת כתובת המייל של המשתמש מה־Session
            String mail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            System.out.println("📨 קיבלנו מייל מהטוקן: " + mail);

            // ✅ שליפת המשתמש מה־DB לפי מייל
            UserEntity user = userService.findByMail(mail);
            if (user == null) {
                System.out.println("⚠️ משתמש לא נמצא עבור המייל: " + mail);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "User not found"
                ));
            }

            System.out.println("🧼 מוחקים צירים עבור userId = " + user.getId());

            // ✅ קריאה למחיקת הצירים
            contractionService.deleteAllByUserId(user.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Contractions deleted successfully"
            ));
        } catch (Exception e) {
            System.out.println("❌ שגיאה במחיקת הצירים:");
            e.printStackTrace(); // 🧠 חשוב מאוד להשאיר זאת כדי לראות את ה־stacktrace המלא

            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Error deleting contractions: " + e.getMessage()
            ));
        }
    }
}
