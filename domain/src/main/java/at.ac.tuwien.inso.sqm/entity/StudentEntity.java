package at.ac.tuwien.inso.sqm.entity;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class StudentEntity extends UisUserEntity {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyPlanRegistration> studyplans = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lehrveranstaltung> dismissedCourses = new ArrayList<>();

    protected StudentEntity() {
    }

    public StudentEntity(String identificationNumber, String name, String email) {
        this(identificationNumber, name, email, null);
    }

    public StudentEntity(String identificationNumber, String name, String email, UserAccountEntity account) {
        super(identificationNumber, name, email, account);
    }

    @Override
    protected void adjustRole(UserAccountEntity account) {
        account.setRole(Rolle.STUDENT);
    }

    public List<StudyPlanRegistration> getStudyplans() {
        return studyplans;
    }

    public StudentEntity addStudyplans(StudyPlanRegistration... studyplans) {
        this.studyplans.addAll(asList(studyplans));
        return this;
    }

    public List<Lehrveranstaltung> getDismissedCourses() {
        return dismissedCourses;
    }

    public void setDismissedCourses(List<Lehrveranstaltung> dismissedCourses) {
        this.dismissedCourses = dismissedCourses;
    }

    public StudentEntity addDismissedCourse(Lehrveranstaltung... dismissedCourse) {
        this.dismissedCourses.addAll(asList(dismissedCourse));
        return this;
    }
}
