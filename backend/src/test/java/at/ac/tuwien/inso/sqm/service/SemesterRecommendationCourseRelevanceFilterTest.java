package at.ac.tuwien.inso.sqm.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import at.ac.tuwien.inso.sqm.entity.StduyPlanEntity;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectForStudyPlanRepository;

@RunWith(MockitoJUnitRunner.class)
public class SemesterRecommendationCourseRelevanceFilterTest {

    private final StudentEntity student = mock(StudentEntity.class);
    private final StduyPlanEntity studyPlan = mock(StduyPlanEntity.class);
    private final Semester registrationSemester = mock(Semester.class);
    private final Subjcet subjectWithoutSemester = mock(Subjcet.class);
    private final Lehrveranstaltung courseWithoutSemester = new Lehrveranstaltung(subjectWithoutSemester);
    private final Subjcet subjectForSemester3 = mock(Subjcet.class);
    private final Lehrveranstaltung courseForSemester3 = new Lehrveranstaltung(subjectForSemester3);
    private final List<Lehrveranstaltung> allCourses = asList(courseWithoutSemester, courseForSemester3);
    @Mock
    private SemestreRepository semesterRepository;
    @Mock
    private SubjectForStudyPlanRepository subjectForStudyPlanRepository;
    @InjectMocks
    private SemesterRecommendationCourseRelevanceFilter filter;

    @Before
    public void setUp() throws Exception {
        when(student.getStudyplans()).thenReturn(singletonList(new StudyPlanRegistration(studyPlan, registrationSemester)));

        when(subjectForStudyPlanRepository
                .findBySubjectInAndStudyPlan(asList(subjectWithoutSemester, subjectForSemester3), studyPlan))
                .thenReturn(asList(
                        new SubjectForStudyPlanEntity(subjectWithoutSemester, studyPlan, true, null),
                        new SubjectForStudyPlanEntity(subjectForSemester3, studyPlan, true, 3)
                ));
    }

    @Test
    public void itKeepsCourseWithoutSemesterRecommendation() throws Exception {
        givenStudentIsInSemester(1);

        List<Lehrveranstaltung> courses = filter.filter(allCourses, student);

        assertThat(courses, equalTo(singletonList(courseWithoutSemester)));
    }

    @Test
    public void itKeepsCourseWithMatchingSemesterRecommendation() throws Exception {
        givenStudentIsInSemester(3);

        List<Lehrveranstaltung> courses = filter.filter(allCourses, student);

        assertThat(courses, equalTo(allCourses));
    }

    private void givenStudentIsInSemester(int semesterNo) {
        List<Semester> semesters = IntStream.range(0, semesterNo).mapToObj(it -> mock(Semester.class)).collect(Collectors.toList());
        when(semesterRepository.findAllSince(registrationSemester)).thenReturn(semesters);
    }
}