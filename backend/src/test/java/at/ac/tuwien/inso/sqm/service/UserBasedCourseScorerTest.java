package at.ac.tuwien.inso.sqm.service;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.inso.sqm.entity.Feedback;
import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.service.course_recommendation.StudentSimilarityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.FeedbackRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserBasedCourseScorerTest {

    @Mock
    private StudentSimilarityService studentSimilarityService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private UserBasedCourseScorer scorer;

    private StudentEntity student = mock(StudentEntity.class);

    private List<StudentEntity> similarStudents = asList(
            mock(StudentEntity.class),
            mock(StudentEntity.class)
    );

    private List<Subjcet> subjects = asList(
            mock(Subjcet.class),
            mock(Subjcet.class),
            mock(Subjcet.class)
    );

    private List<Lehrveranstaltung> courses = asList(
            new Lehrveranstaltung(subjects.get(0)),
            new Lehrveranstaltung(subjects.get(1)),
            new Lehrveranstaltung(subjects.get(2))
    );

    @Test
    public void verifyCourseScoreWithNoSimilarUsers() throws Exception {
        when(studentSimilarityService.getSimilarStudents(student)).thenReturn(emptyList());

        Map<Lehrveranstaltung, Double> scores = scorer.score(courses, student);
        Map<Lehrveranstaltung, Double> expectedScores = courses.stream().collect(toMap(identity(), it -> 0.0));

        assertEquals(expectedScores, scores);
    }

    @Test
    public void verifyCourseScoreWithSimilarUsersAndNoCourseFeedback() throws Exception {
        when(studentSimilarityService.getSimilarStudents(student)).thenReturn(similarStudents);
        when(feedbackRepository.findAllOfStudent(any())).thenReturn(emptyList());
        when(courseRepository.findAllForStudent(similarStudents.get(0))).thenReturn(asList(courses.get(0), courses.get(1)));
        when(courseRepository.findAllForStudent(similarStudents.get(1))).thenReturn(asList(courses.get(1), courses.get(2)));

        Map<Lehrveranstaltung, Double> scores = scorer.score(courses, student);
        Map<Lehrveranstaltung, Double> expectedScores = new HashMap<Lehrveranstaltung, Double>() {
            {
                put(courses.get(0), 1.0);
                put(courses.get(1), 2.0);
                put(courses.get(2), 1.0);
            }
        };

        assertEquals(expectedScores, scores);
    }

    @Test
    public void verifyCourseScoreWithSimilarUserAndCourseFeedback() throws Exception {
        when(studentSimilarityService.getSimilarStudents(student)).thenReturn(similarStudents);
        when(feedbackRepository.findAllOfStudent(similarStudents.get(0))).thenReturn(asList(
                new Feedback(similarStudents.get(0), courses.get(0), Feedback.Type.LIKE),
                new Feedback(similarStudents.get(0), courses.get(2), Feedback.Type.LIKE))
        );
        when(feedbackRepository.findAllOfStudent(similarStudents.get(1))).thenReturn(asList(
                new Feedback(similarStudents.get(1), courses.get(1), Feedback.Type.DISLIKE),
                new Feedback(similarStudents.get(1), courses.get(2), Feedback.Type.LIKE))
        );
        when(courseRepository.findAllForStudent(similarStudents.get(0))).thenReturn(asList(courses.get(0), courses.get(1), courses.get(2)));
        when(courseRepository.findAllForStudent(similarStudents.get(1))).thenReturn(asList(courses.get(1), courses.get(2)));

        Map<Lehrveranstaltung, Double> scores = scorer.score(courses, student);
        Map<Lehrveranstaltung, Double> expectedScores = new HashMap<Lehrveranstaltung, Double>() {
            {
                put(courses.get(0), 2.0);
                put(courses.get(1), 0.0);
                put(courses.get(2), 4.0);
            }
        };

        assertEquals(expectedScores, scores);
    }
}