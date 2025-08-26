package com.ashcollege.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "my_users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String mail;
    private String password;

    // לפי הסכמה הקיימת: עמודה בשם lastPeriodDate (camelCase)
    private LocalDate lastPeriodDate;

    @Column(name = "estimated_due_date")   // עמודה במסד: estimated_due_date
    private LocalDate estimatedDueDate;

    @Column(name = "pregnancy_week")       // עמודה במסד: pregnancy_week
    private Integer pregnancyWeek;

    // ===== שדות נוספים =====
    @Column(name = "number_of_births")
    private Integer numberOfBirths;

    @Column(name = "baby_gender")
    private String babyGender;             // "male" / "female" או null

    @Column(name = "preferred_hospital")
    private String preferredHospital;

    @Column(name = "health_insurance")
    private String healthInsurance;        // "clalit" / "maccabi" / "meuhedet" / "leumit" או null

    // ===== בנאי ריק =====
    public UserEntity() {}

    // ===== Getters / Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getLastPeriodDate() { return lastPeriodDate; }
    public void setLastPeriodDate(LocalDate lastPeriodDate) {
        this.lastPeriodDate = lastPeriodDate;
        updatePregnancyDetails(); // מעדכן Due Date ושבוע הריון אוטומטית
    }

    public LocalDate getEstimatedDueDate() { return estimatedDueDate; }
    public void setEstimatedDueDate(LocalDate estimatedDueDate) { this.estimatedDueDate = estimatedDueDate; }

    public Integer getPregnancyWeek() { return pregnancyWeek; }
    public void setPregnancyWeek(Integer pregnancyWeek) { this.pregnancyWeek = pregnancyWeek; }

    public Integer getNumberOfBirths() { return numberOfBirths; }
    public void setNumberOfBirths(Integer numberOfBirths) { this.numberOfBirths = numberOfBirths; }

    public String getBabyGender() { return babyGender; }
    public void setBabyGender(String babyGender) { this.babyGender = babyGender; }

    public String getPreferredHospital() { return preferredHospital; }
    public void setPreferredHospital(String preferredHospital) { this.preferredHospital = preferredHospital; }

    public String getHealthInsurance() { return healthInsurance; }
    public void setHealthInsurance(String healthInsurance) { this.healthInsurance = healthInsurance; }

    // ===== לוגיקה מחושבת =====
    public void updatePregnancyDetails() {
        if (lastPeriodDate != null) {
            this.estimatedDueDate = lastPeriodDate.plusDays(280); // 40 שבועות
            this.pregnancyWeek = (int) ChronoUnit.WEEKS.between(lastPeriodDate, LocalDate.now());
        }
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mail='" + mail + '\'' +
                ", numberOfBirths=" + numberOfBirths +
                ", babyGender='" + babyGender + '\'' +
                ", preferredHospital='" + preferredHospital + '\'' +
                ", healthInsurance='" + healthInsurance + '\'' +
                '}';
    }
}
