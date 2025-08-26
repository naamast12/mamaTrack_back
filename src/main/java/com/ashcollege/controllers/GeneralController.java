package com.ashcollege.controllers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.ashcollege.dto.AdditionalDetailsDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.ashcollege.entities.UserEntity;
import com.ashcollege.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api")
public class GeneralController {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-days}")
    private long expirationDays;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public GeneralController(UserService userService,
                             PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserEntity user) {
        Map<String, Object> resp = new HashMap<>();
        try {
            userService.registerUser(user);
            resp.put("success", true);
            resp.put("message", "המשתמש נרשם בהצלחה");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "שגיאה במהלך הרישום: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> body) {
        try {
            String mail    = body.get("mail");
            String rawPass = body.get("password");

            UserEntity user = userService.findByMail(mail);
            if (user == null || !passwordEncoder.matches(rawPass, user.getPassword())) {
                return errorResponse("אימייל או סיסמה שגויים", HttpStatus.UNAUTHORIZED);
            }

            // שימוש נכון עם BASE64:
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            String token = Jwts.builder()
                    .setSubject(user.getMail())
                    .setIssuedAt(new Date())
                    .setExpiration(Date.from(Instant.now().plus(expirationDays, ChronoUnit.DAYS)))
                    .signWith(key)
                    .compact();

            System.out.println("✅ Generated JWT = " + token);

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("token", token);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            System.out.println("❌ שגיאה ב־loginUser: " + e.getMessage());
            e.printStackTrace();
            return errorResponse("שגיאה בשרת", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUser() {
        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return errorResponse("משתמש לא מחובר", HttpStatus.UNAUTHORIZED);
        }

        String mail = (String) auth.getPrincipal();
        UserEntity user = userService.findByMail(mail);
        if (user == null) {
            return errorResponse("המשתמש לא נמצא", HttpStatus.NOT_FOUND);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("userId", user.getId());
        resp.put("firstName", user.getFirstName());
        resp.put("lastName", user.getLastName());
        resp.put("mail", user.getMail());
        resp.put("lastPeriodDate", user.getLastPeriodDate());
        resp.put("estimatedDueDate", user.getEstimatedDueDate());
        resp.put("pregnancyWeek", user.getPregnancyWeek());

        // 🆕 הוספת השדות החדשים
        resp.put("numberOfBirths", user.getNumberOfBirths());
        resp.put("babyGender", user.getBabyGender());
        resp.put("preferredHospital", user.getPreferredHospital());
        resp.put("healthInsurance", user.getHealthInsurance());

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest req) {
        return successResponse("התנתקת בהצלחה!");
    }

    @PutMapping("/user/preferences")
    public ResponseEntity<Map<String, Object>> updatePreferences(
            @RequestBody Map<String, Object> body) {

        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return errorResponse("משתמש לא מחובר", HttpStatus.UNAUTHORIZED);
        }

        UserEntity user = userService.findByMail((String) auth.getPrincipal());
        if (user == null) {
            return errorResponse("המשתמש לא נמצא", HttpStatus.NOT_FOUND);
        }

        if (body.get("lastPeriodDate") != null) {
            try {
                LocalDate parsedDate = LocalDate.parse((String) body.get("lastPeriodDate"));
                user.setLastPeriodDate(parsedDate);
            } catch (Exception e) {
                return errorResponse("תאריך לא חוקי", HttpStatus.BAD_REQUEST);
            }
        }

        userService.updateUser(user);

        return successResponse("עודכן בהצלחה");
    }

// הוסף את המתודה הזו ל-GeneralController.java


    @PutMapping("/user/details")
    public ResponseEntity<Map<String, Object>> updateAdditionalDetails(
            @RequestBody AdditionalDetailsDto body,
            Authentication auth) {

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return errorResponse("משתמש לא מחובר", HttpStatus.UNAUTHORIZED);
        }

        // אימייל מה-Authentication (תלוי איך ה-JWT filter בונה את ה-Principal)
        String email = (auth.getPrincipal() instanceof UserDetails)
                ? ((UserDetails) auth.getPrincipal()).getUsername()
                : auth.getName();

        UserEntity user = userService.findByMail(email);
        if (user == null) {
            return errorResponse("המשתמש לא נמצא", HttpStatus.NOT_FOUND);
        }

        // ----- ולידציות ונירמול -----
        Integer births = body.getNumberOfBirths();
        if (births != null) {
            if (births < 0 || births > 20) {
                return errorResponse("מספר לידות לא תקין", HttpStatus.BAD_REQUEST);
            }
            user.setNumberOfBirths(births);
        }

        String gender = normalize(body.getBabyGender());
        if (gender != null && !gender.equals("male") && !gender.equals("female")) {
            return errorResponse("מין עובר לא תקין", HttpStatus.BAD_REQUEST);
        }
        user.setBabyGender(gender); // יכול להיות null (לא ידוע)

        String hospital = normalize(body.getPreferredHospital());
        user.setPreferredHospital(hospital); // יכול להיות null

        String insurance = normalize(body.getHealthInsurance());
        if (insurance != null &&
                !insurance.equals("clalit") &&
                !insurance.equals("maccabi") &&
                !insurance.equals("meuhedet") &&
                !insurance.equals("leumit")) {
            return errorResponse("קופת חולים לא תקינה", HttpStatus.BAD_REQUEST);
        }
        user.setHealthInsurance(insurance); // יכול להיות null

        userService.updateUser(user);

        Map<String, Object> ok = new HashMap<>();
        ok.put("success", true);
        ok.put("message", "פרטים נוספים נשמרו בהצלחה");
        ok.put("numberOfBirths", user.getNumberOfBirths());
        ok.put("babyGender", user.getBabyGender());
        ok.put("preferredHospital", user.getPreferredHospital());
        ok.put("healthInsurance", user.getHealthInsurance());
        return ResponseEntity.ok(ok);
    }

    private String normalize(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
    private ResponseEntity<Map<String, Object>> errorResponse(String msg, HttpStatus status) {
        var resp = new HashMap<String, Object>();
        resp.put("success", false);
        resp.put("message", msg);
        return ResponseEntity.status(status).body(resp);
    }

    private ResponseEntity<Map<String, Object>> successResponse(String msg) {
        var resp = new HashMap<String, Object>();
        resp.put("success", true);
        resp.put("message", msg);
        return ResponseEntity.ok(resp);
    }
}