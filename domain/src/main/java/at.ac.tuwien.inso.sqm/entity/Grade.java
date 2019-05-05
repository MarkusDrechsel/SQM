package at.ac.tuwien.inso.sqm.entity;

import java.util.UUID;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Grade {

    @Id
    @GeneratedValue
    private Long ID;

    @ManyToOne
    private Lehrveranstaltung course;

    @ManyToOne
    private LecturerEntity lecturer;

    @ManyToOne
    private StudentEntity student;

    @Embedded
    private MarkEntity mark;

    private String urlIdentifier = UUID.randomUUID().toString().replace("-", "");

    public Grade() {

    }

    public Grade(Lehrveranstaltung course, LecturerEntity lecturer, StudentEntity student, MarkEntity mark) {
        this.course = course;
        this.lecturer = lecturer;
        this.student = student;
        this.mark = mark;
    }

    public Long getId() {
        return ID;
    }

    public void setId(Long id) {
        this.ID = id;
    }

    public Lehrveranstaltung getCourse() {
        return course;
    }

    public void setCourse(Lehrveranstaltung course) {
        this.course = course;
    }

    public LecturerEntity getLecturer() {
        return lecturer;
    }

    public void setLecturer(LecturerEntity lecturer) {
        this.lecturer = lecturer;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }

    public MarkEntity getMark() {
        return mark;
    }

    public void setMark(MarkEntity mark) {
        this.mark = mark;
    }

    public String getUrlIdentifier() {
        return urlIdentifier;
    }

    public void setUrlIdentifier(String urlIdentifier) {
        this.urlIdentifier = urlIdentifier;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Grade grade = (Grade) o;

        if (ID != null) {
            if (!ID.equals(grade.ID))
                return false;
        } else {
            if (grade.ID != null) {
                return false;
            }
        }
        if (!course.equals(grade.course))
            return false;
        if (!lecturer.equals(grade.lecturer))
            return false;
        if (!student.equals(grade.student))
            return false;
        if (!urlIdentifier.equals(grade.urlIdentifier))
            return false;
        return mark.equals(grade.mark);

    }

    public int hashCode() {
        int result = ID != null ? ID.hashCode() : 0;
        result = course != null ? 31 * result + course.hashCode() : 0;
        result = lecturer != null ? 31 * result + lecturer.hashCode() : 0;
        result = student != null ? 31 * result + student.hashCode() : 0;
        result = mark != null ? 31 * result + mark.hashCode() : 0;
        result = urlIdentifier != null ? 31 * result + urlIdentifier.hashCode() : 0;
        return result;
    }

    public String toString() {
        return "Grade{" +
                "ID=" + ID +
                ", course=" + course +
                ", lecturer=" + lecturer +
                ", student=" + student +
                ", mark=" + mark +
                ", urlIdentifier=" + urlIdentifier +
                '}';
    }
}
