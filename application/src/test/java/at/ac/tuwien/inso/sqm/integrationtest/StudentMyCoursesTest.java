package at.ac.tuwien.inso.sqm.integrationtest;

import at.ac.tuwien.inso.sqm.integrationtest.clock.*;
import at.ac.tuwien.inso.sqm.entity.*;
import at.ac.tuwien.inso.sqm.repository.*;
import at.ac.tuwien.inso.sqm.service.study_progress.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;
import org.springframework.test.context.support.*;
import org.springframework.test.context.transaction.*;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.math.BigDecimal.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@FixedClock("2016-05-05T11:00:00Z")
@TestExecutionListeners({TransactionalTestExecutionListener.class, FixedClockListener.class, DependencyInjectionTestExecutionListener.class})
public class StudentMyCoursesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SemestreRepository semesterRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private LecturerRepository lecturerRepository;
    @Autowired
    private StudyPlanRepository studyPlanRepository;
    @Autowired
    private StduentRepository stduentRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;

    private StudentEntity student;

    private List<Semester> semesters;

    private List<Lehrveranstaltung> courses;

    @Before
    public void setUp() throws Exception {
        student = stduentRepository.save(new StudentEntity("123", "student", "mail@uis.at", new UserAccountEntity("student", "pass", Rolle.STUDENT)));

        prepareSemesters();
        prepareStudyPlans();
        prepareCourses();
        prepareGrades();
        prepareFeedback();
    }

    private void prepareSemesters() {
        semesters = StreamSupport.stream((semesterRepository.save(asList(
                new Semester(2014, SemestreTypeEnum.WinterSemester),
                new Semester(2015, SemestreTypeEnum.SummerSemester),
                new Semester(2015, SemestreTypeEnum.WinterSemester),
                new Semester(2016, SemestreTypeEnum.SummerSemester)
        ))).spliterator(), false).collect(Collectors.toList());
    }

    private void prepareStudyPlans() {
        List<StduyPlanEntity> studyPlans = StreamSupport.stream((studyPlanRepository.save(asList(
                new StduyPlanEntity("study 1", new EtcsDistributionEntity(ONE, ONE, ONE)),
                new StduyPlanEntity("study 2", new EtcsDistributionEntity(ONE, ONE, ONE))
        ))).spliterator(), false).collect(Collectors.toList());

        student.addStudyplans(
                new StudyPlanRegistration(studyPlans.get(0), semesters.get(1)),
                new StudyPlanRegistration(studyPlans.get(1), semesters.get(2))
        );
    }

    private void prepareCourses() {
        Subjcet subject = subjectRepository.save(new Subjcet("subject 1", ONE));

        courses = StreamSupport.stream((courseRepository.save(asList(
                new Lehrveranstaltung(subject, semesters.get(0)),
                new Lehrveranstaltung(subject, semesters.get(1)),
                new Lehrveranstaltung(subject, semesters.get(1)),
                new Lehrveranstaltung(subject, semesters.get(1)),
                new Lehrveranstaltung(subject, semesters.get(3)),
                new Lehrveranstaltung(subject, semesters.get(3)),
                new Lehrveranstaltung(subject, semesters.get(3))
        ))).spliterator(), false).collect(Collectors.toList());

        courses.forEach(it -> it.addStudents(student));
    }

    private void prepareGrades() {
        LecturerEntity lecturer = lecturerRepository.save(new LecturerEntity("456", "lecturer", "lecturer@uis.at"));

        gradeRepository.save(asList(
                new Grade(courses.get(1), lecturer, student, MarkEntity.EXCELLENT),
                new Grade(courses.get(2), lecturer, student, MarkEntity.FAILED),

                new Grade(courses.get(4), lecturer, student, MarkEntity.SUFFICIENT)
        ));
    }

    private void prepareFeedback() {
        feedbackRepository.save(asList(
                new Feedback(student, courses.get(1)),
                new Feedback(student, courses.get(2)),

                new Feedback(student, courses.get(4)),
                new Feedback(student, courses.get(5))
        ));
    }

    @Test
    public void itShowsStudyProgressForStudent() throws Exception {
        mockMvc.perform(
                get("/student/meineLehrveranstaltungen").with(user("student").roles(Rolle.STUDENT.name()))
        ).andExpect(
                model().attribute("studyProgress", new StudyProgress(
                        semesters.get(3).toDto(),
                        asList(
                                new SemesterProgress(semesters.get(3).toDto(), asList(
                                        new CuorseRegistration(courses.get(4), CourseRegistrationState.complete_ok),
                                        new CuorseRegistration(courses.get(5), CourseRegistrationState.needs_grade),
                                        new CuorseRegistration(courses.get(6), CourseRegistrationState.in_progress)
                                )),
                                new SemesterProgress(semesters.get(2).toDto(), emptyList()),
                                new SemesterProgress(semesters.get(1).toDto(), asList(
                                        new CuorseRegistration(courses.get(1), CourseRegistrationState.complete_ok),
                                        new CuorseRegistration(courses.get(2), CourseRegistrationState.complete_not_ok),
                                        new CuorseRegistration(courses.get(3), CourseRegistrationState.needs_feedback)
                                ))
                        )
                ))
        );
    }

}
