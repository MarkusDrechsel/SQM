package at.ac.tuwien.inso.sqm.service;

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

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.FeedbackRepository;
import at.ac.tuwien.inso.sqm.repository.GradeRepository;
import at.ac.tuwien.inso.sqm.repository.StduentRepository;

@RunWith(MockitoJUnitRunner.class)
public class TagFrequencyScorerTests {

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

    @Mock
    private TagFrequencyCalculatorImpl tagFrequencyCalculator;

    @InjectMocks
    private TagFrequencyScoorer tagFrequencyScorer;

    private List<Lehrveranstaltung> courses = asList(
            new Lehrveranstaltung(subject, semester).addTags(new Tag("tag2")),
            new Lehrveranstaltung(subject, semester).addTags(new Tag("tag1"), new Tag("tag4"), new Tag("tag2")),
            new Lehrveranstaltung(subject, semester).addTags(new Tag("tag3")),
            new Lehrveranstaltung(subject, semester).addTags(new Tag("tag2"), new Tag("tag3")),
            new Lehrveranstaltung(subject, semester).addTags(new Tag("tag5"), new Tag("tag2"))
    );

    private Map<Tag, Double> expectedTagFrequencies = new HashMap<Tag, Double>() {
        {
            put(new Tag("tag1"), 0.5);
            put(new Tag("tag2"), 4.3);
            put(new Tag("tag3"), 3.1);
            put(new Tag("tag4"), 0.5);
            put(new Tag("tag5"), -0.5);
        }
    };

    private Map<Lehrveranstaltung, Double> expectedScoredCourses = new HashMap<Lehrveranstaltung, Double>() {
        {
            put(courses.get(3), expectedTagFrequencies.get(new Tag("tag3")) + expectedTagFrequencies.get(new Tag("tag2")));
            put(courses.get(1), expectedTagFrequencies.get(new Tag("tag1")) + expectedTagFrequencies.get(new Tag("tag4")) + expectedTagFrequencies.get(new Tag("tag2")));
            put(courses.get(0), expectedTagFrequencies.get(new Tag("tag2")));
            put(courses.get(2), expectedTagFrequencies.get(new Tag("tag3")));
            put(courses.get(4), expectedTagFrequencies.get(new Tag("tag5")) + expectedTagFrequencies.get(new Tag("tag2")));
        }
    };

    @Before
    public void setUp() throws Exception {
        when(courseRepository.findAllForStudent(student)).thenReturn(courses);
        when(tagFrequencyCalculator.calculate(student)).thenReturn(expectedTagFrequencies);
    }


    @Test
    public void itScoresCoursesByTagFrequency() throws Exception {
        Map<Lehrveranstaltung, Double> scoredCourses = tagFrequencyScorer.score(courses, student);

        assertEquals(expectedScoredCourses, scoredCourses);
    }
}
