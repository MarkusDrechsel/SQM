package at.ac.tuwien.inso.sqm.dto;

import at.ac.tuwien.inso.sqm.entity.Grade;

public class GradeAuhtorizationDTO {

    private Grade grade;
    private String authCode;

    public GradeAuhtorizationDTO() {
    }

    public GradeAuhtorizationDTO(Grade grade) {
        this.grade = grade;
    }

    public GradeAuhtorizationDTO(Grade grade, String code) {
        this.grade = grade;
        this.authCode = code;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
