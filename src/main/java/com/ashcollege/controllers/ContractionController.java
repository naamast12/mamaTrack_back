package com.ashcollege.controllers;

import com.ashcollege.responses.ContractionDto;
import com.ashcollege.service.ContractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contractions")
public class ContractionController {

    private final ContractionService contractionService;

    @Autowired
    public ContractionController(ContractionService contractionService) {
        this.contractionService = contractionService;
    }

    @PostMapping
    public ResponseEntity<?> saveContraction(@RequestBody ContractionDto dto) {
        try {
            ContractionDto saved = contractionService.saveContraction(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Contraction saved successfully",
                    "contraction", saved
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Failed to save contraction: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getContractionsByUserId(@PathVariable Long userId) {
        try {
            List<ContractionDto> contractions = contractionService.getContractionsByUserId(userId);

            if (contractions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "No contractions found for user."
                ));
            }

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
}
