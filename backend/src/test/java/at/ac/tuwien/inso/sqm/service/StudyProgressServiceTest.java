package at.ac.tuwien.inso.sqm.service;

import static at.ac.tuwien.inso.sqm.entity.SemestreTypeEnum.WinterSemester;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.util.Pair;

import at.ac.tuwien.inso.sqm.dto.SemesterDto;
import at.ac.tuwien.inso.sqm.entity.Feedback;
import at.ac.tuwien.inso.sqm.service.study_progress.CuorseRegistration;
import at.ac.tuwien.inso.sqm.service.study_progress.CourseRegistrationState;
import at.ac.tuwien.inso.sqm.service.study_progress.SemesterProgress;
import at.ac.tuwien.inso.sqm.service.study_progress.StudyProgress;
import at.ac.tuwien.inso.sqm.service.study_progress.StudyProgressService;

@RunWith(MockitoJUnitRunner.class)
public class StudyProgressServiceTest {

    @Mock
    private SemesterServiceInterface semesterService;
    @Mock
    private LehrveranstaltungServiceInterface courseService;
    @Mock
    private FeedbackIService feedbackService;
    @Mock
    private GradeIService gradeService;

    @InjectMocks
    private StudyProgressService studyProgressService = new StudyProgressServiceImpl();

    private List<SemesterDto> dtoSemesters;
    private SemesterDto pastSemester;
    private SemesterDto currentSemester;

    private StudentEntity student = new StudentEntity("123", "student", "mail@uis.at");

    @Before
    public void setUp() throws Exception {
        dtoSemesters = new ArrayList<>();
        IntStream.range(1, 5).forEach(it -> {
            SemesterDto semester = new SemesterDto(it, WinterSemester);
            semester.setId(Long.valueOf(it));

            dtoSemesters.add(semester);
        });
        pastSemester = dtoSemesters.get(dtoSemesters.size() - 2);
        currentSemester = dtoSemesters.get(dtoSemesters.size() - 1);

        when(semesterService.getCurrentSemester()).thenReturn(currentSemester);
        when(semesterService.getOrCreateCurrentSemester()).thenReturn(currentSemester);

    }

    @Test
    public void itSetsCurrentSemesterForStudyProgress() throws Exception {
        StudyProgress studyProgress = studyProgressService.studyProgressFor(student);

        assertEquals(currentSemester, studyProgress.getCurrentSemester());
    }

    @Test
    public void studyProgressForStudentWithEmptyStuyPlans() throws Exception {
        StudyProgress studyProgress = studyProgressService.studyProgressFor(student);

        assertEquals(emptyList(), studyProgress.getSemestersProgress());
    }

    @Test
    public void studyProgressForStudentWithEmptyCourseRegistrations() throws Exception {
        prepareStudyPlanRegistrationsFor(dtoSemesters.get(2), dtoSemesters.get(1));

        checkStudentHasSemestersProgress(
                new SemesterProgress(dtoSemesters.get(3), emptyList()),
                new SemesterProgress(dtoSemesters.get(2), emptyList()),
                new SemesterProgress(dtoSemesters.get(1), emptyList())
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private List<StudyPlanRegistration> prepareStudyPlanRegistrationsFor(SemesterDto... semesters) {
        
    	Stream.of(semesters)
                .map(it -> new StudyPlanRegistration(mock(StduyPlanEntity.class), it.toEntity()))
                .forEach(it2 -> student.addStudyplans(it2));
    	
        SemesterDto firstSemester = Stream.of(semesters).min(comparing(SemesterDto::getId)).get();
        List<SemesterDto> studentSemesters = this.dtoSemesters.stream()
                .filter(it -> it.getId() >= firstSemester.getId())
                .collect(Collectors.toList());
        Collections.reverse(studentSemesters);
        List<SemesterDto> studentSemestersDtos = new ArrayList<SemesterDto>();
        for(SemesterDto s : studentSemesters){
        	studentSemestersDtos.add(s);
        }
        
        when(semesterService.findAllSince(firstSemester)).thenReturn(studentSemestersDtos);


        return student.getStudyplans();
    }

    private void checkStudentHasSemestersProgress(SemesterProgress... semestersProgress) {
        StudyProgress studyProgress = studyProgressService.studyProgressFor(student);
        
        assertEquals(asList(semestersProgress), studyProgress.getSemestersProgress());
    }

    @Test
    public void studyProgressForStudentWithOpenCourses() throws Exception {
        prepareStudyPlanRegistrationsFor(pastSemester);
        List<Lehrveranstaltung> courses = prepareCoursesFor(pastSemester, currentSemester);

        checkStudentHasSemestersProgress(
                new SemesterProgress(currentSemester, singletonList(new CuorseRegistration(courses.get(1)))),
                new SemesterProgress(pastSemester, singletonList(new CuorseRegistration(courses.get(0), CourseRegistrationState.needs_feedback)))
        );
    }

    private List<Lehrveranstaltung> prepareCoursesFor(SemesterDto... semesters) {
        List<Lehrveranstaltung> courses = Stream.of(semesters)
                .map(it -> new Lehrveranstaltung(mock(Subjcet.class), it.toEntity()))
                .collect(Collectors.toList());

        when(courseService.findAllForStudent(student)).thenReturn(courses);

        return courses;
    }

    @Test
    public void studyProgressForStudentWithGradedCourses() throws Exception {
        prepareStudyPlanRegistrationsFor(pastSemester);
        List<Lehrveranstaltung> courses = prepareCoursesFor(pastSemester, currentSemester);
        prepareGradesFor(courses.stream().map(it -> Pair.of(it, MarkEntity.EXCELLENT)).collect(Collectors.toList()));

        checkStudentHasSemestersProgress(
                new SemesterProgress(currentSemester, singletonList(new CuorseRegistration(courses.get(1), CourseRegistrationState.needs_feedback))),
                new SemesterProgress(pastSemester, singletonList(new CuorseRegistration(courses.get(0), CourseRegistrationState.needs_feedback)))
        );
    }

    private List<Grade> prepareGradesFor(List<Pair<Lehrveranstaltung, MarkEntity>> coursesWithMarks) {
        List<Grade> grades = coursesWithMarks.stream()
                .map(it -> new Grade(it.getFirst(), mock(LecturerEntity.class), student, it.getSecond()))
                .collect(Collectors.toList());

        when(gradeService.findAllOfStudent(student)).thenReturn(grades);

        return grades;
    }

    @Test
    public void studyProgressForStudentWithFeedbackForCourses() throws Exception {
        prepareStudyPlanRegistrationsFor(pastSemester);
        List<Lehrveranstaltung> courses = prepareCoursesFor(pastSemester, currentSemester);
        prepareFeedbackFor(courses.get(0), courses.get(1));

        checkStudentHasSemestersProgress(
                new SemesterProgress(currentSemester, singletonList(new CuorseRegistration(courses.get(1), CourseRegistrationState.needs_grade))),
                new SemesterProgress(pastSemester, singletonList(new CuorseRegistration(courses.get(0), CourseRegistrationState.needs_grade)))
        );
    }

    private List<Feedback> prepareFeedbackFor(Lehrveranstaltung... courses) {
        List<Feedback> feedback = Stream.of(courses)
                .map(it -> new Feedback(student, it))
                .collect(Collectors.toList());

        when(feedbackService.findAllOfStudent(student)).thenReturn(feedback);

        return feedback;
    }

    @Test
    public void studyProgressForStudentWithGradesAndFeedbackForCourses() throws Exception {
        prepareStudyPlanRegistrationsFor(pastSemester);
        List<Lehrveranstaltung> courses = prepareCoursesFor(pastSemester, currentSemester);
        prepareGradesFor(asList(
                Pair.of(courses.get(0), MarkEntity.EXCELLENT),
                Pair.of(courses.get(1), MarkEntity.FAILED)
        ));
        prepareFeedbackFor(courses.get(0), courses.get(1));

        checkStudentHasSemestersProgress(
                new SemesterProgress(currentSemester, singletonList(new CuorseRegistration(courses.get(1), CourseRegistrationState.complete_not_ok))),
                new SemesterProgress(pastSemester, singletonList(new CuorseRegistration(courses.get(0), CourseRegistrationState.complete_ok)))
        );
    }
}
