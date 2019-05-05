package at.ac.tuwien.inso.sqm.service;

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.Feedback;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.exception.ActionNotAllowedException;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.FeedbackRepository;
import at.ac.tuwien.inso.sqm.service.student_subject_prefs.StudentSubjectPreferenceStore;
import at.ac.tuwien.inso.sqm.validator.FeedbackValidator;
import at.ac.tuwien.inso.sqm.validator.ValidatorFactory;

@Service
public class FeedbackService implements FeedbackIService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);
    private ValidatorFactory validatorFactory = new ValidatorFactory();
    private FeedbackValidator validator = validatorFactory.getFeedbackValidator();

    @Autowired
    StudentSubjectPreferenceStore studentSubjectPreferenceStore;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Feedback> findAllOfStudent(StudentEntity student) {
        return feedbackRepository.findAllOfStudent(student);
    }

    @Override
    @Transactional
    public Feedback save(Feedback feedback) {
        validator.validateNewFeedback(feedback);
        log.info("Creating feedback from student {} for course {}: {} - {}",
                feedback.getStudent().getId(),
                feedback.getCourse().getId(),
                feedback.getType(),
                feedback.getSuggestions());

        guardSingleFeedback(feedback);
        guardStudentRegisteredForCourse(feedback.getStudent(), feedback.getCourse());

        studentSubjectPreferenceStore.studentGaveCourseFeedback(feedback.getStudent(), feedback);

        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> findFeedbackForCourse(Long id) {
    	log.info("finding feedback for course "+id);
    	validator.validateCourseId(id);
        return feedbackRepository.findByCourseId(id);
    }

//    @Override
//  public boolean exists(Feedback feedback) {
//        log.info("check if feedback for student already exists" + feedback.getStudent().getId() + ", " + feedback.getCourse().getId());
//        return feedbackRepository.exists(feedback);
//    }

    private void guardSingleFeedback(Feedback feedback) {
    	log.info("guading single feedback, if no warn log line follows its fine.");
        if (feedbackRepository.exists(feedback)) {
        	log.warn("Giving feedback multiple times for the same course is not allowed");
            throw new ActionNotAllowedException("Giving feedback multiple times for the same course is not allowed");
        }
    }

    private void guardStudentRegisteredForCourse(StudentEntity student, Lehrveranstaltung course) {
    	log.info("guarding student is registered for course already");
        if (!courseRepository.existsCourseRegistration(student, course)) {
        	log.warn("StudentEntity tried to give feedback for course he is not registered for");
            throw new ActionNotAllowedException("StudentEntity tried to give feedback for course he is not registered for");
        }
    }
}
