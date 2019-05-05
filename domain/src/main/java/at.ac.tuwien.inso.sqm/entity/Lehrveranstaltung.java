package at.ac.tuwien.inso.sqm.entity;

import at.ac.tuwien.inso.sqm.dto.SemesterDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

@Entity
public class Lehrveranstaltung {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private Subjcet subject;

    @ManyToOne(optional = false)
    private Semester semester;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String decsription;

    @Column(nullable = false)
    private int studentLimitts;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Tag> tags = new ArrayList<>();

    @ManyToMany
    private List<StudentEntity> students = new ArrayList<>();

    protected Lehrveranstaltung() {
    }
    
    public Lehrveranstaltung(Subjcet subject){
    	this(subject, null, "");
    }

    public Lehrveranstaltung(Subjcet subject, Semester semester) {
        this(subject, semester, "");
    }
    
    public Lehrveranstaltung(Subjcet subject, SemesterDto semester) {
        this(subject, semester.toEntity(), "");
    }

    public Lehrveranstaltung(Subjcet subject, Semester semester, String description) {
        this.subject = subject;
        this.semester = semester;
        this.decsription = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Subjcet getSubject() {
        return subject;
    }

    public void setSubject(Subjcet subject) {
        this.subject = subject;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public String getDescription() {
        return decsription;
    }

    public void setDescription(String description) {
        this.decsription = description;
    }

    public List<Tag> getTags() {
        return unmodifiableList(tags);
    }

    public List<StudentEntity> getStudents() {
        return students;
    }

    public Lehrveranstaltung addTags(Tag... tags) {
        this.tags.addAll(asList(tags));
        return this;
    }

    public void removeTags(List<Tag> tags) {
        this.tags.removeAll(tags);
    }

    public Lehrveranstaltung addStudents(StudentEntity... students) {
        this.students.addAll(asList(students));
        return this;
    }

    public void removeStudents(StudentEntity... students) {
        this.students.removeAll(asList(students));
    }

    public int getStudentLimits() {
        return studentLimitts;
    }

    public Lehrveranstaltung setStudentLimits(int studentLimits) {
        this.studentLimitts = studentLimits;
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lehrveranstaltung course = (Lehrveranstaltung) o;

        if (studentLimitts != course.studentLimitts) return false;
        if (id != null ? !id.equals(course.id) : course.id != null) return false;
        if (subject != null ? !subject.equals(course.subject) : course.subject != null) return false;
        if (semester != null ? !semester.equals(course.semester) : course.semester != null) return false;
        if (decsription != null ? !decsription.equals(course.decsription) : course.decsription != null) return false;
        if (tags != null ? !tags.equals(course.tags) : course.tags != null) return false;
        return students != null ? students.equals(course.students) : course.students == null;

    }

    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
        result = 31 * result + (decsription != null ? decsription.hashCode() : 0);
        result = 31 * result + studentLimitts;
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (students != null ? students.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Lehrveranstaltung{" +
                "id=" + id +
                ", subject=" + subject +
                ", semester=" + semester +
                ", decsription='" + decsription + '\'' +
                ", tags=" + tags +
                ", student limits=" + studentLimitts +
                ", students=" + students +
                '}';
    }
}