package at.ac.tuwien.inso.sqm.service;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import at.ac.tuwien.inso.sqm.dto.CoruseDetailsForStudent;
import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import at.ac.tuwien.inso.sqm.exception.BusinessObjectNotFoundException;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectForStudyPlanRepository;

@RunWith(MockitoJUnitRunner.class)
public class CourseDetailsTest {

    private Semester olderSemester = new Semester(2015, SemestreTypeEnum.WinterSemester);
    private Semester currentSemester = new Semester(2016, SemestreTypeEnum.WinterSemester);

    private Subjcet subject = new Subjcet("subject", BigDecimal.ONE);

    private StudentEntity student = new StudentEntity("1", "student", "student@uis.at");

    private Long courseId = 1L;
    private Lehrveranstaltung course = new Lehrveranstaltung(subject, currentSemester, "some description");

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private SemesterServiceInterface semesterService;
    @Mock
    private SubjectForStudyPlanRepository subjectForStudyPlanRepository;
    @InjectMocks
    private LehrveranstaltungServiceInterface courseService = new CourseServiceImpl();

    @Before
    public void setUp() throws Exception {

        when(semesterService.getCurrentSemester()).thenReturn(currentSemester.toDto());
        when(semesterService.getOrCreateCurrentSemester()).thenReturn(currentSemester.toDto());

        when(subjectForStudyPlanRepository.findBySubject(subject)).thenReturn(emptyList());

        when(courseRepository.findOne(courseId)).thenReturn(course);
    }

    @Test(expected = BusinessObjectNotFoundException.class)
    public void itThrowsOnUnknownCourse() throws Exception {
        courseService.courseDetailsFor(student, 2L);
    }

    @Test
    public void testCanEnrollForCourseInOlderSemester() throws Exception {
        when(courseRepository.findOne(courseId)).thenReturn(new Lehrveranstaltung(subject, olderSemester));

        CoruseDetailsForStudent details = courseService.courseDetailsFor(student, courseId);

        assertFalse(details.getCanEnroll());
    }

    @Test
    public void testCanEnrollForCourseAlreadyRegisteredFor() throws Exception {
        when(courseRepository.existsCourseRegistration(student, course)).thenReturn(true);

        CoruseDetailsForStudent details = courseService.courseDetailsFor(student, courseId);

        assertFalse(details.getCanEnroll());
    }

    @Test
    public void testCanEnrollForCourseInCurrentSemesterNotRegisteredFor() throws Exception {
        when(courseRepository.existsCourseRegistration(student, course)).thenReturn(false);

        CoruseDetailsForStudent details = courseService.courseDetailsFor(student, courseId);

        assertTrue(details.getCanEnroll());
    }

    @Test
    public void testGeneralDetails() throws Exception {
        CoruseDetailsForStudent details = courseService.courseDetailsFor(student, courseId);

        assertEquals(subject.getName(), details.getName());
        assertEquals(subject.getEcts(), details.getEcts());
        assertEquals(currentSemester.getLabel(), details.getSemester());
        assertEquals(course.getDescription(), details.getDescription());
    }

    @Test
    public void testTags() throws Exception {
        course.addTags(new Tag("tag 1"), new Tag("tag 2"));
        List<String> tags = course.getTags().stream().map(Tag::getName).collect(toList());

        CoruseDetailsForStudent details = courseService.courseDetailsFor(student, courseId);

        assertEquals(tags, details.getTags());
    }

    @Test
    public void testLecturers() throws Exception {
        subject.addLecturers(new LecturerEntity("2", "lecturer", "lecturer@uis.at"));

        CoruseDetailsForStudent details = courseService.courseDetailsFor(student, courseId);

        assertEquals(subject.getLecturers(), details.getLecturers());
    }

    @Test
    public void testStudyPlans() throws Exception {
        List<SubjectForStudyPlanEntity> studyplans = singletonList(mock(SubjectForStudyPlanEntity.class));
        when(subjectForStudyPlanRepository.findBySubject(subject)).thenReturn(studyplans);

        CoruseDetailsForStudent details = courseService.courseDetailsFor(student, courseId);

        assertEquals(studyplans, details.getStudyplans());
    }

}
