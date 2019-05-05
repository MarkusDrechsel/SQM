package at.ac.tuwien.inso.sqm.service;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.service.course_recommendation.CourseScorer;
import at.ac.tuwien.inso.sqm.service.course_recommendation.CourseRelevanceFilter;

@RunWith(MockitoJUnitRunner.class)
public class RecommendationServiceTests {

    @Mock
    private StudentEntity student;

    @Mock
    private Subjcet subject;

    @Mock
    private Semester semester;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private SemestreRepository semesterRepository;

    @Mock
    private CourseNormalizer courseNormalizer;

    private List<CourseRelevanceFilter> filters = asList(
            mock(CourseRelevanceFilter.class),
            mock(CourseRelevanceFilter.class)
    );

    private List<CourseScorer> scorers = asList(
            mock(CourseScorer.class),
            mock(CourseScorer.class)
    );

    @InjectMocks
    private RecommmendationService recommendationService;

    private HashMap<String, Semester> semesters = new HashMap<String, Semester>() {
        {
            put("WS16", new Semester(2016, SemestreTypeEnum.WinterSemester));
        }
    };

    private List<Lehrveranstaltung> courses = asList(
            new Lehrveranstaltung(subject, semesters.get("WS16")).addTags(new Tag("tag1")),
            new Lehrveranstaltung(subject, semesters.get("WS16")).addTags(new Tag("tag2")),
            new Lehrveranstaltung(subject, semesters.get("WS16")).addTags(new Tag("tag3")),
            new Lehrveranstaltung(subject, semesters.get("WS16")).addTags(new Tag("tag4")),
            new Lehrveranstaltung(subject, semesters.get("WS16")).addTags(new Tag("tag5"))
    );

    private Map<Lehrveranstaltung, Double> scoredCoursesByTagFrequency = new HashMap<Lehrveranstaltung, Double>() {
        {
            put(courses.get(0), 3.1);
            put(courses.get(2), 7.4);
            put(courses.get(1), 4.3);
            put(courses.get(3), 3.8);
            put(courses.get(4), 5.3);
        }
    };

    @Before
    public void setUp() throws Exception {
        when(courseRepository.findAllRecommendableForStudent(student)).thenReturn(courses);
        when(semesterRepository.findFirstByOrderByIdDesc()).thenReturn(semesters.get("WS16"));

        scorers.forEach(it -> {
            when(it.score(courses, student)).thenReturn(scoredCoursesByTagFrequency);
            when(it.weight()).thenReturn(1.0);
        });
        recommendationService.setCourseScorers(scorers);

        filters.forEach(it -> when(it.filter(courses, student)).thenReturn(courses));
        recommendationService.setCourseRelevanceFilters(filters);
    }

    @Test
    public void itRecommendsCoursesByScoring() throws Exception {
        List<Lehrveranstaltung> recommendedCourses = recommendationService.recommendCourses(student);

        List<Lehrveranstaltung> expected = asList(
                courses.get(2), courses.get(4), courses.get(1), courses.get(3), courses.get(0)
        );

        assertEquals(expected, recommendedCourses);
    }

    @Test
    public void itFiltersCoursesBeforeRecommendingThem() throws Exception {
        List<Lehrveranstaltung> coursesAfterFilter1 = courses.stream().skip(1).collect(Collectors.toList());
        when(filters.get(0).filter(courses, student)).thenReturn(coursesAfterFilter1);

        List<Lehrveranstaltung> coursesAfterFilter2 = coursesAfterFilter1.stream().skip(1).collect(Collectors.toList());
        when(filters.get(1).filter(coursesAfterFilter1, student)).thenReturn(coursesAfterFilter2);

        scorers.forEach(it -> when(it.score(coursesAfterFilter2, student)).thenReturn(scoredCoursesByTagFrequency));

        List<Lehrveranstaltung> recommendedCourses = recommendationService.recommendCourses(student);

        assertThat(recommendedCourses, not(hasItems(courses.get(0), courses.get(1))));
    }
}
