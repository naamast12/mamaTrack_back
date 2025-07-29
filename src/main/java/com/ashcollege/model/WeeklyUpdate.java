package com.ashcollege.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// אופציונלי – כדי לא להחזיר null-ים
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeeklyUpdate {
    private Integer week;
    private Short trimester;
    private String fetalDevelopment;
    private String maternalChanges;
    private List<String> symptoms;
    private List<String> nutrition;
    private List<String> exercise;
    private List<String> tips;
    private List<String> redFlags;
    private BabySize babySize;

    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }

    public Short getTrimester() {
        if (trimester != null) return trimester;
        if (week == null) return null;
        if (week <= 13) return 1;
        if (week <= 27) return 2;
        return 3;
    }
    public void setTrimester(Short trimester) { this.trimester = trimester; }

    public String getFetalDevelopment() { return fetalDevelopment; }
    public void setFetalDevelopment(String fetalDevelopment) { this.fetalDevelopment = fetalDevelopment; }

    public String getMaternalChanges() { return maternalChanges; }
    public void setMaternalChanges(String maternalChanges) { this.maternalChanges = maternalChanges; }

    public List<String> getSymptoms() { return symptoms; }
    public void setSymptoms(List<String> symptoms) { this.symptoms = symptoms; }

    public List<String> getNutrition() { return nutrition; }
    public void setNutrition(List<String> nutrition) { this.nutrition = nutrition; }

    public List<String> getExercise() { return exercise; }
    public void setExercise(List<String> exercise) { this.exercise = exercise; }

    public List<String> getTips() { return tips; }
    public void setTips(List<String> tips) { this.tips = tips; }

    public List<String> getRedFlags() { return redFlags; }
    public void setRedFlags(List<String> redFlags) { this.redFlags = redFlags; }

    public BabySize getBabySize() { return babySize; }
    public void setBabySize(BabySize babySize) { this.babySize = babySize; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(Include.NON_NULL) // אופציונלי
    public static class BabySize {
        private String label;
        private String emoji;     // ← חדש
//        private String emojiAlt;  // ← אופציונלי, fallback
        private Double lengthCm;
        private Double weightGr;

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public String getEmoji() { return emoji; }
        public void setEmoji(String emoji) { this.emoji = emoji; }

//        public String getEmojiAlt() { return emojiAlt; }
//        public void setEmojiAlt(String emojiAlt) { this.emojiAlt = emojiAlt; }

        public Double getLengthCm() { return lengthCm; }
        public void setLengthCm(Double lengthCm) { this.lengthCm = lengthCm; }

        public Double getWeightGr() { return weightGr; }
        public void setWeightGr(Double weightGr) { this.weightGr = weightGr; }
    }
}
