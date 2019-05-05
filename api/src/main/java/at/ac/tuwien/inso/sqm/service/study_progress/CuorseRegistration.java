package at.ac.tuwien.inso.sqm.service.study_progress; //FIXME package naming convention?!

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.entity.Grade;

public class CuorseRegistration {

    private Lehrveranstaltung course;

    private CourseRegistrationState state;

	private Grade grade;

    public CuorseRegistration(Lehrveranstaltung course) {
        this(course, CourseRegistrationState.in_progress);
    }

    public CuorseRegistration(Lehrveranstaltung course, CourseRegistrationState state) {
        this.course = course;
        this.state = state;
    }
    
    public CuorseRegistration(Lehrveranstaltung course, CourseRegistrationState state, Grade grade) {
        this.course = course;
        this.state = state;
	    this.grade = grade;
    }

    public Lehrveranstaltung getCourse() {
        return course;
    }

    public CourseRegistrationState getState() {
        return state;
    }
    
    public Grade getGrade(){
    	return grade;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CuorseRegistration that = (CuorseRegistration) o;

        if (getCourse() != null ? !getCourse().equals(that.getCourse()) : that.getCourse() != null) return false;
        return getState() == that.getState();

    }

    public int hashCode() {
        int result = getCourse() != null ? getCourse().hashCode() : 0;
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        return result;
    }

    public String toString() {
        return "CuorseRegistration{" +
                "course=" + course +
                ", state=" + state +
                '}';
    }
}
