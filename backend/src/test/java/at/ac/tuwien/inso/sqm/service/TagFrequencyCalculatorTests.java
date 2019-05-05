package at.ac.tuwien.inso.sqm.service;

import static at.ac.tuwien.inso.sqm.service.TagFrequencyCalculatorImpl.feedbackWeights;
import static at.ac.tuwien.inso.sqm.service.TagFrequencyCalculatorImpl.gradeWeights;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import at.ac.tuwien.inso.sqm.entity.Feedback;
import at.ac.tuwien.inso.sqm.entity.Feedback.Type;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.FeedbackRepository;
import at.ac.tuwien.inso.sqm.repository.GradeRepository;
import at.ac.tuwien.inso.sqm.repository.StduentRepository;

@RunWith(MockitoJUnitRunner.class)
public class TagFrequencyCalculatorTests {

    @Mock
    private StduentRepository stduentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private StudentEntity student;
    @Mock
    private Subjcet subject;
    @Mock
    private Semester semester;
    @Mock
    private LecturerEntity lecturer;
    @InjectMocks
    private TagFrequencyCalculatorImpl tagFrequencyCalculator;
    private List<Lehrveranstaltung> courses = asList(
            new Lehrveranstaltung(subject, semester).addTags(new Tag("tag2")),
            new Lehrveranstaltung(subject, semester).addTags(new Tag("tag1"), new Tag("tag4"), new Tag("tag2")),
            new Lehrveranstaltung(subject, semester).addTags(new Tag("tag3")),
            new Lehrveranstaltung(subject, semester).addTags(new Tag("tag2"), new Tag("tag3")),
            new Lehrveranstaltung(subject, semester).addTags(new Tag("tag5"), new Tag("tag2"))
    );

    private List<Grade> grades = asList(
            new Grade(courses.get(0), lecturer, student, MarkEntity.GOOD),
            new Grade(courses.get(1), lecturer, student, MarkEntity.EXCELLENT),
            new Grade(courses.get(2), lecturer, student, MarkEntity.SUFFICIENT),
            new Grade(courses.get(4), lecturer, student, MarkEntity.FAILED)
    );

    private List<Feedback> feedbacks = asList(
            new Feedback(student, courses.get(0), Type.LIKE),
            new Feedback(student, courses.get(1), Type.DISLIKE),
            new Feedback(student, courses.get(3), Type.LIKE),
            new Feedback(student, courses.get(4), Type.DISLIKE)
    );

    private Map<Tag, Double> tagFrequencies = new HashMap<Tag, Double>() {
        {
            put(new Tag("tag1"), 1.0);
            put(new Tag("tag2"), 4.0);
            put(new Tag("tag3"), 2.0);
            put(new Tag("tag4"), 1.0);
            put(new Tag("tag5"), 1.0);
        }
    };

    private Map<Tag, Double> tagFrequenciesWithGrades = new HashMap<Tag, Double>() {
        {
            put(new Tag("tag1"), gradeWeights.get(MarkEntity.EXCELLENT));
            put(new Tag("tag2"), gradeWeights.get(MarkEntity.GOOD) + gradeWeights.get(MarkEntity.EXCELLENT) + gradeWeights.get(MarkEntity.FAILED));
            put(new Tag("tag3"), gradeWeights.get(MarkEntity.SUFFICIENT));
            put(new Tag("tag4"), gradeWeights.get(MarkEntity.EXCELLENT));
            put(new Tag("tag5"), gradeWeights.get(MarkEntity.FAILED));
        }
    };

    private Map<Tag, Double> getTagFrequenciesWithFeedback = new HashMap<Tag, Double>() {
        {
            put(new Tag("tag1"), feedbackWeights.get(Type.DISLIKE));
            put(new Tag("tag2"), feedbackWeights.get(Type.LIKE) + feedbackWeights.get(Type.DISLIKE) + feedbackWeights.get(Type.LIKE) + feedbackWeights.get(Type.DISLIKE));
            put(new Tag("tag3"), feedbackWeights.get(Type.LIKE));
            put(new Tag("tag4"), feedbackWeights.get(Type.DISLIKE));
            put(new Tag("tag5"), feedbackWeights.get(Type.DISLIKE));
        }
    };

    private Map<Tag, Double> expectedTagFrequencies = new HashMap<Tag, Double>() {
        {
            put(new Tag("tag1"), tagFrequencies.get(new Tag("tag1")) + tagFrequenciesWithGrades.get(new Tag("tag1")) + getTagFrequenciesWithFeedback.get(new Tag("tag1")));
            put(new Tag("tag2"), tagFrequencies.get(new Tag("tag2")) + tagFrequenciesWithGrades.get(new Tag("tag2")) + getTagFrequenciesWithFeedback.get(new Tag("tag2")));
            put(new Tag("tag3"), tagFrequencies.get(new Tag("tag3")) + tagFrequenciesWithGrades.get(new Tag("tag3")) + getTagFrequenciesWithFeedback.get(new Tag("tag3")));
            put(new Tag("tag4"), tagFrequencies.get(new Tag("tag4")) + tagFrequenciesWithGrades.get(new Tag("tag4")) + getTagFrequenciesWithFeedback.get(new Tag("tag4")));
            put(new Tag("tag5"), tagFrequencies.get(new Tag("tag5")) + tagFrequenciesWithGrades.get(new Tag("tag5")) + getTagFrequenciesWithFeedback.get(new Tag("tag5")));
        }
    };

    @Before
    public void setUp() throws Exception {
        when(courseRepository.findAllForStudent(student)).thenReturn(courses);
        when(gradeRepository.findAllOfStudent(student)).thenReturn(grades);
        when(feedbackRepository.findAllOfStudent(student)).thenReturn(feedbacks);
    }

    @Test
    public void itCalculatesTagFrequencies() throws Exception {
        Map<Tag, Double> calculatedTagFrequencies = tagFrequencyCalculator.calculate(student);

        assertEquals(expectedTagFrequencies, calculatedTagFrequencies);
    }
}
