package at.ac.tuwien.inso.sqm.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import at.ac.tuwien.inso.sqm.entity.Feedback;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.service.course_recommendation.StudentSimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.FeedbackRepository;
import at.ac.tuwien.inso.sqm.service.course_recommendation.CourseScorer;

@Service
public class UserBasedCourseScorer implements CourseScorer {

    private static final double LIKE = 2;
    private static final double DISLIKE = -1;
    private static final double REGISTERED = 1;

    @Autowired
    private StudentSimilarityService studentSimilarityService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;;

    @Value("${uis.course.recommender.user.scorer.weight:1}")
    private Double weight;

    @Override
    public double weight() {
        return weight;
    }

    @Override
    public Map<Lehrveranstaltung, Double> score(List<Lehrveranstaltung> courses, StudentEntity student) {
        Map<Lehrveranstaltung, Double> courseScores = courses.stream().collect(toMap(identity(), it -> 0.0));

        List<StudentEntity> similarStudents = studentSimilarityService.getSimilarStudents(student);

        courseScores.keySet().forEach(course ->
                similarStudents.forEach(similarStudent -> {
                            List<Feedback> similarStudentFeedback = feedbackRepository.findAllOfStudent(similarStudent);

                                /*
                                 * get all courses from similar user for the student course subject
                                 *
                                 * there might be more courses in case the similar student did more courses for the subject
                                 */
                            List<Lehrveranstaltung> matchingCourses = courseRepository.findAllForStudent(similarStudent).stream()
                                    .filter(studentCourse -> studentCourse.getSubject().equals(course.getSubject()))
                                    .collect(Collectors.toList());

                            Double score = similarStudentFeedback.stream()
                                    .filter(feedback -> matchingCourses.contains(feedback.getCourse()))
                                    .mapToDouble(feedback -> feedback.getType().equals(Feedback.Type.LIKE) ? LIKE : DISLIKE)
                                    .sum();


                            // the similar user did not give feedback for this course but he was registered to this course
                            if (score != null && score == 0 && matchingCourses.size() > 0)
                                score = REGISTERED;

                            courseScores.put(course, courseScores.get(course) + score);
                        }
                )
        );

        return courseScores;
    }
}
