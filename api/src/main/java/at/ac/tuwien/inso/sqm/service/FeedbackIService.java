package at.ac.tuwien.inso.sqm.service;

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.Feedback;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public interface FeedbackIService {

	/**
	 * returns all feedback of a student, student should not be null
	 *
	 * @param student
	 * @return
	 */
    @PreAuthorize("isAuthenticated()")
    List<Feedback> findAllOfStudent(StudentEntity student);

    /**
     * saves a new feedback
     * @param feedback should not be null, feedback.suggestions and feedback.type should not be null. if there is no correct validation a ValidationException will be thrown
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    Feedback save(Feedback feedback);

    /**
     * returns all feedback for a course with the given id
     * @param id should not be null or <1, if integrationtest is, a ValidationException will be thrown
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    List<Feedback> findFeedbackForCourse(Long id);


//    /**
//     * Checks if the given student has already rated this course.
//     * @param feedback should not be null
//     * @return true, if the student has already given feedback for this course, else false
//     */
//    @PreAuthorize("isAuthenticated()")
//    boolean exists(Feedback feedback);
}
