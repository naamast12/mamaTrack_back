// src/main/java/com/ash college/dto/AdditionalDetailsDto.java
package com.ashcollege.dto;

public class AdditionalDetailsDto {
    private Integer numberOfBirths;        // FE שולח מספר או null
    private String  babyGender;            // "male"/"female"/"" (או null)
    private String  preferredHospital;     // טקסט חופשי או null
    private String  healthInsurance;       // "clalit"/"maccabi"/"meuhedet"/"leumit"/"" (או null)

    public Integer getNumberOfBirths() { return numberOfBirths; }
    public void setNumberOfBirths(Integer numberOfBirths) { this.numberOfBirths = numberOfBirths; }
    public String getBabyGender() { return babyGender; }
    public void setBabyGender(String babyGender) { this.babyGender = babyGender; }
    public String getPreferredHospital() { return preferredHospital; }
    public void setPreferredHospital(String preferredHospital) { this.preferredHospital = preferredHospital; }
    public String getHealthInsurance() { return healthInsurance; }
    public void setHealthInsurance(String healthInsurance) { this.healthInsurance = healthInsurance; }
}
