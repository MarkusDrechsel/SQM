package at.ac.tuwien.inso.sqm.service.student_subject_prefs; //FIXME package naming convention?!

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//TODO why is that entity in module 'api'??
@Document
public class StudentSubjectPreference {

    @Id
    public String id;

    private Long studentId;

    private Long subjectId;;

    private Double preferenceValue;

    protected StudentSubjectPreference() {

    }

    public StudentSubjectPreference(Long studentId, Long subjectId, Double preferenceValue) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.preferenceValue = preferenceValue;
    }

    public String toString() {
        return "StudentSubjectPreference{" +
                "id='" + id + '\'' +
                ", studentId=" + getStudentId() +
                ", subjectId=" + getSubjectId() +
                ", preferenceValue=" + getPreferenceValue() +
                '}';
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public Double getPreferenceValue() {
        return preferenceValue;
    }

    public StudentSubjectPreference setPreferenceValue(Double preferenceValue) {
        this.preferenceValue = preferenceValue;
        return this;
    }
}
