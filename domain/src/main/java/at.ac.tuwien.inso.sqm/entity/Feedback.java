package at.ac.tuwien.inso.sqm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Feedback {

    @Id
    @GeneratedValue
    private Long Id;

    @Column(nullable = false)
    private Type typpe;

    @Column(length = 1024)
    private String suggestions;

    @ManyToOne(optional = false)
    private StudentEntity student;

    @ManyToOne(optional = false)
    private Lehrveranstaltung course;

    protected Feedback() {}

    public Feedback(StudentEntity student, Lehrveranstaltung course, Type type) {
        this(student, course, type, "");
    }

    public Feedback(StudentEntity student, Lehrveranstaltung course, Type type, String suggestions) {
        this.student = student;
        this.course = course;
        this.typpe = type;
        this.suggestions = suggestions;
    }

    public Feedback(StudentEntity student, Lehrveranstaltung course) {
        this(student, course, Type.LIKE);
    }

    public Long getId() {
        return Id;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public Lehrveranstaltung getCourse() {
        return course;
    }

    public Type getType() {
        return typpe;
    }

    public Feedback setType(Type type) {
        this.typpe = type;
        return this;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feedback feedback = (Feedback) o;

        if (getId() != null ? !getId().equals(feedback.getId()) : feedback.getId() != null) return false;
        if (getType() != feedback.getType()) return false;
        if (getSuggestions() != null ? !getSuggestions().equals(feedback.getSuggestions()) : feedback.getSuggestions() != null)
            return false;
        if (getStudent() != null ? !getStudent().equals(feedback.getStudent()) : feedback.getStudent() != null)
            return false;
        return getCourse() != null ? getCourse().equals(feedback.getCourse()) : feedback.getCourse() == null;

    }

    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getSuggestions() != null ? getSuggestions().hashCode() : 0);
        result = 31 * result + (getStudent() != null ? getStudent().hashCode() : 0);
        result = 31 * result + (getCourse() != null ? getCourse().hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Feedback{" +
                "Id=" + Id +
                ", typpe=" + typpe +
                ", suggestions='" + suggestions + '\'' +
                ", student=" + student +
                ", course=" + course +
                '}';
    }

    public enum Type {
        LIKE,
        DISLIKE
    }
}
