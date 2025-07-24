//package com.ashcollege.controllers;
//
//import com.ashcollege.entities.UserEntity;
//import com.ashcollege.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.time.LocalDate;
//import java.util.*;
//
//@RestController
//public class GeneralController {
//
//    @Value("${app.jwt.secret}")
//    private String secret;
//
//    @Value("${app.jwt.expiration-days}")
//    private long expirationDays;
//
//    @Autowired
//    private UserService userService;
//
//    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
//    public String hello() {
//        return "Hello From Server";
//    }
//
//    @PostMapping("/api/register")
//    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserEntity user) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            userService.registerUser(user);
//            response.put("success", true);
//            response.put("message", "המשתמש נרשם בהצלחה");
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", "שגיאה במהלך הרישום: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @PostMapping("/api/login")
//    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginData,
//                                                         HttpServletRequest request) {
//        String mail = loginData.get("mail");
//        String password = loginData.get("password");
//
//        try {
//            UserEntity foundUser = userService.findByMail(mail);
//            if (foundUser == null) {
//                return errorResponse("המשתמש לא נמצא", HttpStatus.UNAUTHORIZED);
//            }
//            if (!userService.checkPassword(password, foundUser.getPassword())) {
//                return errorResponse("הסיסמה שגויה", HttpStatus.UNAUTHORIZED);
//            }
//
//            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//
//            UsernamePasswordAuthenticationToken auth =
//                    new UsernamePasswordAuthenticationToken(foundUser.getMail(), null, authorities);
//
//            SecurityContextHolder.getContext().setAuthentication(auth);
//            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
//
//            // Build user data for response
//            Map<String, Object> userData = new HashMap<>();
//            userData.put("id", foundUser.getId());
//            userData.put("firstName", foundUser.getFirstName());
//            userData.put("lastName", foundUser.getLastName());
//            userData.put("mail", foundUser.getMail());
//            userData.put("lastPeriodDate", foundUser.getLastPeriodDate());
//            userData.put("estimatedDueDate", foundUser.getEstimatedDueDate());
//            userData.put("pregnancyWeek", foundUser.getPregnancyWeek());
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "המשתמש התחבר בהצלחה");
//            response.put("user", userData);
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            return errorResponse("שגיאה בכניסה: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PostMapping("/api/logout")
//    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
//        SecurityContextHolder.clearContext(); // מנקה את האובייקט המאחסן את ה-Authentication
//        request.getSession(false).invalidate(); // הורג את ה־Session כך שאינו תקף עוד
//
//        Map<String, Object> resp = new HashMap<>();
//        resp.put("success", true);
//        resp.put("message", "התנתקת בהצלחה!");
//        return ResponseEntity.ok(resp);
//    }
//
//
//    @GetMapping("/api/user")
//    public ResponseEntity<Map<String, Object>> getUser() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated()) {
//            return errorResponse("משתמש לא מחובר", HttpStatus.UNAUTHORIZED);
//        }
//
//        String userMail = (String) auth.getPrincipal();
//        UserEntity user = userService.findByMail(userMail);
//        if (user == null) {
//            return errorResponse("המשתמש לא נמצא", HttpStatus.NOT_FOUND);
//        }
//
//
//        System.out.println(user);  // הדפסת אובייקט המשתמש
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("userId", user.getId());
//        response.put("firstName", user.getFirstName());
//        response.put("lastName", user.getLastName());
//        response.put("mail", user.getMail());
//        response.put("lastPeriodDate", user.getLastPeriodDate());
//        response.put("estimatedDueDate", user.getEstimatedDueDate());
//        response.put("pregnancyWeek", user.getPregnancyWeek());
//
//        return ResponseEntity.ok(response);
//    }
//
//
//    private ResponseEntity<Map<String, Object>> errorResponse(String message, HttpStatus status) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", false);
//        response.put("message", message);
//        return ResponseEntity.status(status).body(response);
//    }
//
//    private ResponseEntity<Map<String, Object>> successResponse(String message) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", message);
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/api/user/preferences")
//    public ResponseEntity<Map<String,Object>> updatePreferences(@RequestBody Map<String,Object> body){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if(auth == null || !auth.isAuthenticated()) return errorResponse("משתמש לא מחובר", HttpStatus.UNAUTHORIZED);
//
//        UserEntity user = userService.findByMail((String) auth.getPrincipal());
//        if(user == null) return errorResponse("המשתמש לא נמצא", HttpStatus.NOT_FOUND);
//
//
//        // 🟡 נוסיף כאן את התמיכה בתאריך וסת:
//        if (body.get("lastPeriodDate") != null) {
//            try {
//                LocalDate parsedDate = LocalDate.parse((String) body.get("lastPeriodDate"));
//                user.setLastPeriodDate(parsedDate); // זה יפעיל גם updatePregnancyDetails
//            } catch (Exception e) {
//                return errorResponse("תאריך לא חוקי", HttpStatus.BAD_REQUEST);
//            }
//        }
//
//        userService.updateUser(user);
//
//        return successResponse("עודכן בהצלחה");
//    }
//
//
//}

package com.ashcollege.controllers;

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
//
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> body) {
//        try {
//            String mail = body.get("mail");
//            String rawPass = body.get("password");
//
//            UserEntity user = userService.findByMail(mail);
//
//
//// 🔍 הדפסות דיאגנוסטיות:
//            System.out.println("✅ Saved Password = " + (user != null ? user.getPassword() : "null"));
//            System.out.println("✅ Raw Password = " + rawPass);
//            System.out.println("✅ Password Match = " +
//                    (user != null ? passwordEncoder.matches(rawPass, user.getPassword()) : "false"));
//
//            if (user == null || !passwordEncoder.matches(rawPass, user.getPassword())) {
//                return errorResponse("אימייל או סיסמה שגויים", HttpStatus.UNAUTHORIZED);
//            }
//
//            // יצירת טוקן JWT
//            byte[] keyBytes = Decoders.BASE64.decode(secret);
//            SecretKey key = Keys.hmacShaKeyFor(keyBytes);
//
//            String token = Jwts.builder()
//                    .setSubject(user.getMail())
//                    .setIssuedAt(new Date())
//                    .setExpiration(Date.from(Instant.now().plus(expirationDays, ChronoUnit.DAYS)))
//                    .signWith(key)
//                    .compact();
//
//            System.out.println("✅ Generated JWT = " + token);
//
//            // בניית תגובה עם הטוקן והפרטים של המשתמש
//            Map<String, Object> userData = new HashMap<>();
//            userData.put("id", user.getId());
//            userData.put("firstName", user.getFirstName());
//            userData.put("lastName", user.getLastName());
//            userData.put("mail", user.getMail());
//            userData.put("lastPeriodDate", user.getLastPeriodDate());
//            userData.put("estimatedDueDate", user.getEstimatedDueDate());
//            userData.put("pregnancyWeek", user.getPregnancyWeek());
//
//            Map<String, Object> resp = new HashMap<>();
//            resp.put("success", true);
//            resp.put("token", token);
//            resp.put("user", userData);
//
//            return ResponseEntity.ok(resp);
//
//        } catch (Exception e) {
//            System.out.println("❌ שגיאה ב־loginUser: " + e.getMessage());
//            e.printStackTrace();
//            return errorResponse("שגיאה בשרת", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


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

