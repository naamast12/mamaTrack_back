package com.ashcollege.service;

import com.ashcollege.entities.HospitalChecklistEntity;
import com.ashcollege.entities.UserEntity;
import com.ashcollege.repository.HospitalChecklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HospitalChecklistService {
    private final HospitalChecklistRepository hospitalChecklistRepository;
    private final UserService userService;

    @Autowired
    public HospitalChecklistService(HospitalChecklistRepository hospitalChecklistRepository, UserService userService) {
        this.hospitalChecklistRepository = hospitalChecklistRepository;
        this.userService = userService;
    }

    public HospitalChecklistEntity saveChecklist(String itemsStatusJson) {
        Long userId = getCurrentUserId();

        Optional<HospitalChecklistEntity> existing = hospitalChecklistRepository.findByUserId(userId);

        if (existing.isPresent()) {
            HospitalChecklistEntity entity = existing.get();
            entity.setItemsStatus(itemsStatusJson);
            return hospitalChecklistRepository.save(entity);
        } else {
            HospitalChecklistEntity entity = new HospitalChecklistEntity();
            entity.setUserId(userId);
            entity.setItemsStatus(itemsStatusJson);
            return hospitalChecklistRepository.save(entity);
        }
    }

    public HospitalChecklistEntity getChecklistForCurrentUser() {
        Long userId = getCurrentUserId();
        Optional<HospitalChecklistEntity> existing = hospitalChecklistRepository.findByUserId(userId);

        if (existing.isPresent()) {
            return existing.get();
        } else {
            String defaultJson = createResetJson();
            HospitalChecklistEntity entity = new HospitalChecklistEntity();
            entity.setUserId(userId);
            entity.setItemsStatus(defaultJson);
            return hospitalChecklistRepository.save(entity);
        }
    }

    public HospitalChecklistEntity resetChecklist() {
        Long userId = getCurrentUserId();
        String resetJson = createResetJson();

        Optional<HospitalChecklistEntity> existing = hospitalChecklistRepository.findByUserId(userId);

        if (existing.isPresent()) {
            HospitalChecklistEntity entity = existing.get();
            entity.setItemsStatus(resetJson);
            return hospitalChecklistRepository.save(entity);
        } else {
            HospitalChecklistEntity entity = new HospitalChecklistEntity();
            entity.setUserId(userId);
            entity.setItemsStatus(resetJson);
            return hospitalChecklistRepository.save(entity);
        }
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        Object principal = auth.getPrincipal();
        System.out.println("🔐 principal = " + principal);

        try {
            if (principal instanceof String) {
                String value = (String) principal;
                System.out.println("🔍 Looking for user with mail: " + value);
                
                // נסה למצוא משתמש לפי המייל
                UserEntity user = userService.findByMail(value);
                if (user == null) {
                    System.err.println("❌ User not found for mail: " + value);
                    throw new IllegalStateException("User not found");
                }
                System.out.println("✅ Found user: " + user.getFirstName() + " " + user.getLastName() + " (ID: " + user.getId() + ")");
                return user.getId();
            } else {
                throw new IllegalStateException("Unknown principal type: " + principal.getClass());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to extract user ID from principal");
        }
    }

    // ✅ מימוש מלא ל־JSON ברירת המחדל - רשימת ציוד לחדר לידה
    private String createResetJson() {
        return "[" +
                "{\"id\": 1, \"name\": \"מברשת שיניים\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 2, \"name\": \"משחת שיניים\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 3, \"name\": \"שמפו ומרכך\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 4, \"name\": \"סבון/תחליב רחצה\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 5, \"name\": \"דאורדורנט\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 6, \"name\": \"מסרק\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 7, \"name\": \"שפתון לחות\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 8, \"name\": \"סבון פנים וקרם לחות\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 9, \"name\": \"מגבות רחצה (2-3)\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 10, \"name\": \"חיתולים לספיגת דימום\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 11, \"name\": \"פדים לספיגת דימום\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 12, \"name\": \"מגבונים לחים\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 13, \"name\": \"קרם לחות לגוף\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 14, \"name\": \"מראה קטנה\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 15, \"name\": \"קליפס לשיער\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 16, \"name\": \"ספריי לשיער\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 17, \"name\": \"קרם ידיים\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 18, \"name\": \"מברשת שיער\", \"category\": \"היגיינה וטיפוח\", \"checked\": false}," +
                "{\"id\": 19, \"name\": \"בגד ליציאה הביתה\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 20, \"name\": \"חלוק\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 21, \"name\": \"פיגמה/כותונת לילה (עם פתח קדמי אם מניקה)\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 22, \"name\": \"נעלי בית/כפכפים\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 23, \"name\": \"גרביים (2-3 זוגות)\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 24, \"name\": \"תחתוני הריון (מספר זוגות)\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 25, \"name\": \"חזיית הנקה (2-3 אם מניקה)\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 26, \"name\": \"גופיות להנקה (3-4)\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 27, \"name\": \"סוודר או קארדיגן\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 28, \"name\": \"כובע או צעיף (לחורף)\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 29, \"name\": \"גרביונים חמים\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 30, \"name\": \"בגדים נוחים ללידה\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 31, \"name\": \"חזייה רגילה (אם לא מניקה)\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 32, \"name\": \"תחתונים חד פעמיים\", \"category\": \"בגדים (לאם)\", \"checked\": false}," +
                "{\"id\": 33, \"name\": \"כרטיס קופת חולים\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 34, \"name\": \"תעודת זהות\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 35, \"name\": \"טפסי הרשמה מוקדמת לבית החולים (אם יש)\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 36, \"name\": \"תוכנית לידה (אם יש)\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 37, \"name\": \"רשימת תרופות עדכנית\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 38, \"name\": \"כרטיס ביטוח\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 39, \"name\": \"פנקס חיסונים\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 40, \"name\": \"תוצאות בדיקות הריון\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 41, \"name\": \"אישור רופא (אם נדרש)\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 42, \"name\": \"כרטיסי אשראי/כסף\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 43, \"name\": \"רשימת טלפונים חשובים\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 44, \"name\": \"מחברת ועט\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 45, \"name\": \"תרופות אישיות (אם יש)\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 46, \"name\": \"ויטמינים להריון\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 47, \"name\": \"מסמכי ביטוח חיים\", \"category\": \"רפואי ומסמכים\", \"checked\": false}," +
                "{\"id\": 48, \"name\": \"טיטולים\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 49, \"name\": \"שמיכת תינוק\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 50, \"name\": \"בגד ליציאה הביתה לתינוק\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 51, \"name\": \"גרביים\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 52, \"name\": \"כובע\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 53, \"name\": \"שמיכות עיטוף (1-2)\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 54, \"name\": \"חיתולים\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 55, \"name\": \"מגבונים\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 56, \"name\": \"סלקל\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 57, \"name\": \"בגדים נוספים לתינוק (3-4 סטים)\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 58, \"name\": \"כפפות לתינוק\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 59, \"name\": \"שמיכה דקה לתינוק\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 60, \"name\": \"בגדים חמים לתינוק (לחורף)\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 61, \"name\": \"חיתול בד (אם משתמשים)\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 62, \"name\": \"קרם לחות לתינוק\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 63, \"name\": \"שמן לתינוק\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 64, \"name\": \"מגבות רחצה לתינוק\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 65, \"name\": \"בקבוק (אם לא מניקה)\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 66, \"name\": \"פורמולה (אם לא מניקה)\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 67, \"name\": \"פטמה לבקבוק\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 68, \"name\": \"מחמם בקבוקים\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 69, \"name\": \"מגבות נייר לתינוק\", \"category\": \"מוצרים לתינוק\", \"checked\": false}," +
                "{\"id\": 70, \"name\": \"שמיכה עבה לתינוק (לחורף)\", \"category\": \"מוצרים לתינוק\", \"checked\": false}" +
                "]";
    }

    public void deleteChecklistForCurrentUser() {
        Long userId = getCurrentUserId();
        Optional<HospitalChecklistEntity> existing = hospitalChecklistRepository.findByUserId(userId);

        if (existing.isPresent()) {
            hospitalChecklistRepository.delete(existing.get());
        }
    }
} 