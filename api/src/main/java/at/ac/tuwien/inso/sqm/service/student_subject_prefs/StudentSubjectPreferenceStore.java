package at.ac.tuwien.inso.sqm.service.student_subject_prefs; //FIXME package naming convention?!

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.entity.Feedback;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;

public interface StudentSubjectPreferenceStore {

	/**
	 * adds the student and the course to the StudentSubjectPreferenceRepository
	 * means that the student has a preference for the subject of the course because he registered integrationtest
	 * 
	 * @param student should not be null and id should not be null
	 * @param course should not be null, should have an subject that is not null and that subject should have an id that is not null
	 */
    void studentRegisteredCourse(StudentEntity student, Lehrveranstaltung course);

    /**
     * removes the preference of a student for a subject (the subject for the given course) from the StudentSubjectPreferenceRepository
     * 
     * @param student should not be null and id should not be null
	 * @param course should not be null, should have an subject that is not null and that subject should have an id that is not null
     */
    void studentUnregisteredCourse(StudentEntity student, Lehrveranstaltung course);

    /**
     * is updating student subject preference due to course feedback
     * 
     * @param student should not be null and id should not be null
     * @param feedback should have a valid feedback.getType()
     */
    void studentGaveCourseFeedback(StudentEntity student, Feedback feedback);
}
