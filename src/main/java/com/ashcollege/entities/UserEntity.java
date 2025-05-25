package com.ashcollege.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "my_users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;
    private String mail;
    private String password;
    private LocalDate lastPeriodDate;
    private LocalDate estimatedDueDate;
    private Integer pregnancyWeek;

    public UserEntity() {
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getLastPeriodDate() {
        return lastPeriodDate;
    };

    public void setLastPeriodDate(LocalDate lastPeriodDate) {
        this.lastPeriodDate = lastPeriodDate;
        updatePregnancyDetails(); // חישוב תאריך לידה ושבוע הריון אוטומטי
    }

    public LocalDate getEstimatedDueDate() {
        return estimatedDueDate;
    }
    public void setEstimatedDueDate(LocalDate estimatedDueDate) {
        this.estimatedDueDate = estimatedDueDate;
    }

    public Integer getPregnancyWeek() {
        return pregnancyWeek;
    }
    public void setPregnancyWeek(Integer pregnancyWeek) {
        this.pregnancyWeek = pregnancyWeek;
    }

    public void updatePregnancyDetails() {
        if (lastPeriodDate != null) {
            this.estimatedDueDate = lastPeriodDate.plusDays(280);
            this.pregnancyWeek = (int) java.time.temporal.ChronoUnit.WEEKS.between(lastPeriodDate, LocalDate.now());
        }
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
