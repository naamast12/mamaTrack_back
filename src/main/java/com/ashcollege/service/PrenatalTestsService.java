package com.ashcollege.service;

import com.ashcollege.model.PrenatalTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrenatalTestsService {

    private static final Logger log = LoggerFactory.getLogger(PrenatalTestsService.class);

    @Value("classpath:data/prenatal-tests.json")
    private Resource dataFile;

    // ברירת מחדל: רשימה ריקה כדי שלא נקבל NPE אם טעינה נכשלה
    private List<PrenatalTest> cache = Collections.emptyList();

    @PostConstruct
    public void load() {
        try (InputStream is = dataFile.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            cache = mapper.readValue(is, new TypeReference<List<PrenatalTest>>() {});
            log.info("Loaded {} prenatal tests from JSON", cache.size());
        } catch (Exception e) {
            log.error("Failed to load prenatal-tests.json from classpath", e);
            cache = Collections.emptyList(); // לא מפיל את השרת
        }
    }

    public List<PrenatalTest> getAll() {
        return cache;
    }

    public List<PrenatalTest> byWeek(int week) {
        return cache.stream()
                .filter(t -> t.getWeekFrom() != null && t.getWeekTo() != null
                        && week >= t.getWeekFrom() && week <= t.getWeekTo())
                .collect(Collectors.toList());
    }

    public List<PrenatalTest> byTrimester(short trimester) {
        int[] range = trimesterRange(trimester); // [start, end] בשבועות
        int start = range[0], end = range[1];

        return cache.stream()
                .filter(t -> {
                    boolean explicit = t.getTrimester() != null && t.getTrimester() == trimester;
                    boolean hasWeeks = t.getWeekFrom() != null && t.getWeekTo() != null;
                    boolean overlaps = hasWeeks && t.getWeekTo() >= start && t.getWeekFrom() <= end;
                    // נציג בדיקה אם הוגדרה מפורשות לטרימסטר הזה
                    // או אם טווח השבועות שלה חוצה את טווח הטרימסטר
                    return explicit || overlaps;
                })
                .collect(Collectors.toList());
    }

    /** טווחי השבועות לכל טרימסטר (אפשר לשנות לפי ההגדרה שלך) */
    private int[] trimesterRange(short trimester) {
        switch (trimester) {
            case 1: return new int[]{1, 12};   // טרימסטר ראשון: שבוע 1–12
            case 2: return new int[]{13, 27};  // טרימסטר שני: שבוע 13–27
            default: return new int[]{28, 40}; // טרימסטר שלישי: שבוע 28–40
        }
    }


    public List<PrenatalTest> byRange(int from, int to) {
        return cache.stream()
                .filter(t -> t.getWeekFrom() != null && t.getWeekTo() != null
                        && t.getWeekTo() >= from && t.getWeekFrom() <= to)
                .collect(Collectors.toList());
    }

}
