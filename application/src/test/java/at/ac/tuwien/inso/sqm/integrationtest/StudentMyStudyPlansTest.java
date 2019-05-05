package at.ac.tuwien.inso.sqm.integrationtest;

import static java.util.Arrays.asList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.GradeRepository;
import at.ac.tuwien.inso.sqm.repository.LecturerRepository;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.repository.StduentRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class StudentMyStudyPlansTest extends AbstractStudyPlansTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SemestreRepository semesterRepository;

    @Autowired
    private StduentRepository stduentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    private Semester ws;
    private List<Lehrveranstaltung> courses;

    @Before
    public void setUp() {
        super.setUp();
        ws = semesterRepository.save(new Semester(2016, SemestreTypeEnum.WinterSemester));
        Iterable<Lehrveranstaltung> courses = courseRepository.save(asList(
                new Lehrveranstaltung(this.subjects.get(0), ws),
                new Lehrveranstaltung(this.subjects.get(1), ws),
                new Lehrveranstaltung(this.subjects.get(2), ws),
                new Lehrveranstaltung(this.subjects.get(3), ws),
                new Lehrveranstaltung(this.subjects.get(4), ws),
                new Lehrveranstaltung(this.subjects.get(5), ws)
        ));
        this.courses = StreamSupport.stream(courses.spliterator(), false).collect(Collectors.toList());
    }

    @Test
    public void studentShouldSeeOwnStudyPlansTest() throws Exception {

        // given a student and study plan registrations
        studyPlanRepository.save(asList(studyPlan1, studyPlan2, studyPlan3));
        UserAccountEntity user =  new UserAccountEntity("caroline", "pass", Rolle.STUDENT);
        StudentEntity s = new StudentEntity("s1123960", "Caroline Black", "caroline.black@uis.at", user);
        s.addStudyplans(new StudyPlanRegistration(studyPlan1, ws), new StudyPlanRegistration(studyPlan3, ws));
        stduentRepository.save(s);
        StudyPlanRegistration sReg1 = s.getStudyplans().get(0);
        StudyPlanRegistration sReg3 = s.getStudyplans().get(1);

        // the student should see own study plans
        mockMvc.perform(
                get("/student/my-studyplans").with(user(user))
        ).andExpect(
                model().attribute("studyPlanRegistrations", asList(sReg1, sReg3))
        );

    }

    @Test
    public void studentShouldSeeDetailsOfOwnStudyPlansTest() throws Exception {

        // given subjects (in a study plan) and grades of a student
        StduyPlanEntity studyPlan = studyPlanRepository.save(studyPlan2);
        SubjectForStudyPlanEntity s1 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(0), studyPlan, true, 1));
        SubjectForStudyPlanEntity s2 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(1), studyPlan, false, 1));
        SubjectForStudyPlanEntity s3 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(2), studyPlan, true, 2));
        SubjectForStudyPlanEntity s4 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(3), studyPlan, false, 3));

        UserAccountEntity user =  new UserAccountEntity("caroline", "pass", Rolle.STUDENT);
        StudentEntity s = new StudentEntity("s1123960", "Caroline Black", "caroline.black@uis.at", user);
        s.addStudyplans(new StudyPlanRegistration(studyPlan, ws));
        stduentRepository.save(s);
        LecturerEntity l = lecturerRepository.save(new LecturerEntity("l1234563", "lecturer", "email"));

        Grade g1 = gradeRepository.save(new Grade(courses.get(0), l, s, MarkEntity.GOOD));
        Grade g3 = gradeRepository.save(new Grade(courses.get(2), l, s, MarkEntity.FAILED));
        Grade g4 = gradeRepository.save(new Grade(courses.get(3), l, s, MarkEntity.GOOD));
        Grade g5 = gradeRepository.save(new Grade(courses.get(4), l, s, MarkEntity.GOOD));
        Grade g6 = gradeRepository.save(new Grade(courses.get(5), l, s, MarkEntity.FAILED));

        // the student should see details of the study plan, containing separate lists of mandatory and optional subjects and grades
        mockMvc.perform(
                get("/student/my-studyplans").param("id", studyPlan.getId().toString()).with(user(user))
        ).andExpect(
                model().attribute("studyPlan", studyPlan)
        ).andExpect(
                model().attribute("mandatory", asList(new SubjectWithGrade(s1, g1, SubjectTyppe.mandatory), new SubjectWithGrade(s3, g3, SubjectTyppe.mandatory)))
        ).andExpect(
                model().attribute("optional", asList(new SubjectWithGrade(s2, SubjectTyppe.optional), new SubjectWithGrade(s4, g4, SubjectTyppe.optional)))
        ).andExpect(
                model().attribute("freeChoice", asList(new SubjectWithGrade(g5, SubjectTyppe.FREE_CHOICE), new SubjectWithGrade(g6, SubjectTyppe.FREE_CHOICE)))
        ).andExpect(
                model().attribute("progressMandatory", 3.0)
        ).andExpect(
                model().attribute("progressOptional", 3.0)
        ).andExpect(
                model().attribute("progressFreeChoice", 6.0)
        );
    }


}
