package at.ac.tuwien.inso.sqm.integrationtest;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.inso.sqm.entity.*;
import at.ac.tuwien.inso.sqm.repository.*;
import org.jboss.aerogear.security.otp.Totp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class LecturerCourseDetailsTest {


    UserAccountEntity user1 = new UserAccountEntity("lecturer1", "pass", Rolle.LECTURER);
    LecturerEntity lecturer1 = new LecturerEntity("l0001", "LecturerEntity 1", "email1@uis.at", user1);
    LecturerEntity lecturer2 = new LecturerEntity("l0002", "LecturerEntity 2", "email2@uis.at", new UserAccountEntity("lecturer2", "pass", Rolle.LECTURER));
    LecturerEntity lecturer3 = new LecturerEntity("l0003", "LecturerEntity 3", "email3@uis.at", new UserAccountEntity("lecturer3", "pass", Rolle.LECTURER));
    StudentEntity student = new StudentEntity("st1", "StudentEntity", "st@ude.nt", new UserAccountEntity("st1", "pass", Rolle.STUDENT));
    Semester ss2016 = new Semester(2016, SemestreTypeEnum.SummerSemester);
    Semester ws2016 = new Semester(2016, SemestreTypeEnum.WinterSemester);
    Subjcet calculus = new Subjcet("Calculus", new BigDecimal(3.0));
    Subjcet sepm = new Subjcet("SEPM", new BigDecimal(6.0));
    Subjcet ase = new Subjcet("ASE", new BigDecimal(6.0));
    Lehrveranstaltung sepmSS2016 = new Lehrveranstaltung(sepm, ss2016);
    Lehrveranstaltung sepmWS2016 = new Lehrveranstaltung(sepm, ws2016);
    Lehrveranstaltung aseWS2016 = new Lehrveranstaltung(ase, ws2016);
    Lehrveranstaltung calculusWS2016 = new Lehrveranstaltung(calculus, ws2016);
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
    private TagRepository tagRepository;
    @Autowired
    private StduentRepository stduentRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private StudyPlanRepository studyPlanRepository;

    @Before
    public void setUp() {

        student = stduentRepository.save(student);

        lecturer1 = lecturerRepository.save(lecturer1);
        lecturer2 = lecturerRepository.save(lecturer2);
        lecturer3 = lecturerRepository.save(lecturer3);

        semesterRepository.save(ss2016);
        semesterRepository.save(ws2016);

        subjectRepository.save(calculus);
        calculus.addLecturers(lecturer3);
        subjectRepository.save(sepm);
        sepm.addLecturers(lecturer1);
        subjectRepository.save(ase);
        ase.addLecturers(lecturer1, lecturer2);

        sepmSS2016 = courseRepository.save(sepmSS2016);
        sepmWS2016 = courseRepository.save(sepmWS2016);
        aseWS2016 = courseRepository.save(aseWS2016);
        calculusWS2016 = courseRepository.save(calculusWS2016);


        aseWS2016.getStudents().add(student);

        tagRepository.save(asList(
                new Tag("Computer Science"),
                new Tag("Math"),
                new Tag("Fun"),
                new Tag("Easy"),
                new Tag("Difficult")
        ));
    }

    @Test
    public void lecturersShouldSeeTheirIssuedGradesInCourseTest() throws Exception {

        // given course ase held by lecturer1 and lecturer2 and 3 registered students
        StudentEntity st2 = new StudentEntity("st2", "gradeStudent2", "st2@ude.nt", new UserAccountEntity("gradest2", "pass", Rolle.STUDENT));
        StudentEntity st3 = new StudentEntity("st3", "gradeStudent3", "st3@ude.nt", new UserAccountEntity("gradest3", "pass", Rolle.STUDENT));
        StudentEntity st4 = new StudentEntity("st4", "gradeStudent3", "st4@ude.nt", new UserAccountEntity("gradest4", "pass", Rolle.STUDENT));
        stduentRepository.save(asList(st2, st3, st4));
        aseWS2016.addStudents(st2, st3, st4);

        // when: lecturer1 issues grades for st1 and st2
        //       lecturer2 issues grades for st3
        Grade gr1 = gradeRepository.save(new Grade(aseWS2016, lecturer1, st2, MarkEntity.EXCELLENT));
        Grade gr2 = gradeRepository.save(new Grade(aseWS2016, lecturer1, st3, MarkEntity.FAILED));
        Grade gr3 = gradeRepository.save(new Grade(aseWS2016, lecturer2, st4, MarkEntity.GOOD));

        // then lecturer1 and lecturer2 should see their issued grades
        mockMvc.perform(
                get("/lecturer/course-details/issued-grades")
                        .param("courseId", aseWS2016.getId().toString())
                        .with(user("lecturer1").roles(Rolle.LECTURER.name()))
                        .with(csrf())
        ).andExpect(
                model().attribute("course", aseWS2016)
        ).andExpect(
                model().attribute("grades", asList(gr1, gr2))
        );

        mockMvc.perform(
                get("/lecturer/course-details/issued-grades")
                        .param("courseId", aseWS2016.getId().toString())
                        .with(user("lecturer2").roles(Rolle.LECTURER.name()))
                        .with(csrf())
        ).andExpect(
                model().attribute("course", aseWS2016)
        ).andExpect(
                model().attribute("grades", asList(gr3))
        );

    }

    @Test
    public void lecturersShouldSeeRegisteredStudentsToCourseTest() throws Exception {
        // given lecturer1 and lecturer2 of the sepm course

        // when a student has registered to the sepm course
        sepmSS2016.addStudents(student);

        //lecturer1 should see the registered student
        mockMvc.perform(
                get("/lecturer/course-details/registrations")
                        .param("courseId", sepmSS2016.getId().toString())
                        .with(user("lecturer1").roles(Rolle.LECTURER.name()))
                        .with(csrf())
        ).andExpect(
                model().attribute("course", sepmSS2016)
        ).andExpect(
                model().attribute("students", asList(student))
        );

        //lecturer2 should see the registered student too
        mockMvc.perform(
                get("/lecturer/course-details/registrations")
                        .param("courseId", sepmSS2016.getId().toString())
                        .with(user("lecturer2").roles(Rolle.LECTURER.name()))
                        .with(csrf())
        ).andExpect(
                model().attribute("course", sepmSS2016)
        ).andExpect(
                model().attribute("students", asList(student))
        );

    }

    @Test
    public void lecturerShouldSeeFeedback() throws Exception {

        // given course ase held by lecturer1 and students student1 and student2 with grades in ase
        StudentEntity st1 = stduentRepository.save(new StudentEntity("stud1", "gradeStudent2", "st1@ude.nt", new UserAccountEntity("gradestud1", "pass", Rolle.STUDENT)));
        StudentEntity st2 = stduentRepository.save(new StudentEntity("stud2", "gradeStudent3", "st2@ude.nt", new UserAccountEntity("gradestud2", "pass", Rolle.STUDENT)));
        Subjcet ase = subjectRepository.save(new Subjcet("ASE", new BigDecimal(6.0)));
        Semester ws2016 = semesterRepository.save(new Semester(2016, SemestreTypeEnum.WinterSemester));
        Lehrveranstaltung aseWS2016 = courseRepository.save(new Lehrveranstaltung(ase, ws2016).addStudents(st1,st2));
        gradeRepository.save(new Grade(aseWS2016, lecturer1, st1, MarkEntity.EXCELLENT));
        gradeRepository.save(new Grade(aseWS2016, lecturer1, st2, MarkEntity.GOOD));

        // when student1 and student2 give feedback
        Feedback feedback1 = new Feedback(
                st1,
                aseWS2016,
                Feedback.Type.DISLIKE, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec enim ligula. " +
                "Sed eget posuere tellus. Aenean fermentum maximus tempor. Ut ultricies dapibus nulla vitae mollis. " +
                "Suspendisse a nunc nisi. Sed ut sapien eu odio sodales laoreet eu ac turpis. " +
                "In id sapien id ante sollicitudin consectetur at laoreet mi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Suspendisse quam sem, ornare eget pellentesque sit amet, tincidunt id metus. Sed scelerisque neque sed laoreet elementum. " +
                "Integer eros neque, vulputate a hendrerit at, ullamcorper in orci. Donec sit amet risus hendrerit, hendrerit magna non, dapibus nibh. " +
                "Suspendisse sed est feugiat, dapibus ante non, aliquet neque. Cras magna sapien, pharetra ut ante ut, malesuada hendrerit erat. " +
                "Mauris fringilla mattis dapibus. Nullam iaculis nunc in tortor gravida, id tempor justo elementum.");
        Feedback feedback2 = new Feedback(
                st2,
                aseWS2016,
                Feedback.Type.LIKE, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec enim ligula. " +
                "Sed eget posuere tellus. Aenean fermentum maximus tempor. Ut ultricies dapibus nulla vitae mollis. " +
                "Suspendisse a nunc nisi. Sed ut sapien eu odio sodales laoreet eu ac turpis. " +
                "In id sapien id ante sollicitudin consectetur at laoreet mi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Suspendisse quam sem, ornare eget pellentesque sit amet, tincidunt id metus. Sed scelerisque neque sed laoreet elementum. " +
                "Integer eros neque, vulputate a hendrerit at, ullamcorper in orci. Donec sit amet risus hendrerit, hendrerit magna non, dapibus nibh. " +
                "Suspendisse sed est feugiat, dapibus ante non, aliquet neque. Cras magna sapien, pharetra ut ante ut, malesuada hendrerit erat. " +
                "Mauris fringilla mattis dapibus. Nullam iaculis nunc in tortor gravida, id tempor justo elementum.");

        feedbackRepository.save(asList(feedback1, feedback2));

        // the lecturer should see the feedback
        mockMvc.perform(
                get("/lecturer/course-details/feedback")
                        .param("courseId", aseWS2016.getId().toString())
                        .with(user("lecturer1").roles(Rolle.LECTURER.name()))
                        .with(csrf())
        ).andExpect(
                model().attribute("course", aseWS2016)
        ).andExpect(
                model().attribute("feedbacks", asList(feedback1, feedback2))
        );

    }

    @Test
    public void lecturerShouldSeeCourseDetailsTest() throws Exception {

        // given course "maths" in study plan "SE" with some tags with "lecturer1"
        Tag tag1 = new Tag("math");
        Tag tag2 = new Tag("calculus");
        tagRepository.save(asList(tag1, tag2));
        Subjcet maths = subjectRepository.save(new Subjcet("maths", new BigDecimal(6.0)));
        maths.addLecturers(lecturer1);
        Lehrveranstaltung mathsCourse =  new Lehrveranstaltung(maths, ws2016, "some description");
        mathsCourse.setStudentLimits(1);
        mathsCourse.addTags(tag1, tag2);
        courseRepository.save(mathsCourse);
        StduyPlanEntity studyPlan = studyPlanRepository.save(new StduyPlanEntity("SE", new EtcsDistributionEntity(new BigDecimal(60.0), new BigDecimal(30.0), new BigDecimal(30.0))));
        SubjectForStudyPlanEntity subjectForStudyPlan = new SubjectForStudyPlanEntity(maths, studyPlan, true);
        studyPlan.addSubjects(subjectForStudyPlan);
        List<SubjectForStudyPlanEntity> expectedSubjectForStudyPlanList = new ArrayList<>();
        expectedSubjectForStudyPlanList.add(subjectForStudyPlan);

        // the lecturer should see the course details
        mockMvc.perform(
                get("/lecturer/course-details")
                        .param("courseId", mathsCourse.getId().toString())
                        .with(user("lecturer1").roles(Rolle.LECTURER.name()))
                        .with(csrf())
        ).andExpect(
                model().attribute("course", mathsCourse)
        ).andExpect(
                model().attribute("studyPlans", expectedSubjectForStudyPlanList)
        );
    }

    @Test
    public void issueGradeSuccessTest() throws Exception {

        // given "student" and "lecturer1" with the course "ase"
        Totp totp = new Totp(lecturer1.getTwoFactorSecret());

        // when "lecturer1" issues a grade for "student"
        mockMvc.perform(
                post("/lecturer/course-details/addGrade")
                        .with(user("lecturer1").roles(Rolle.LECTURER.name()))
                        .param("courseId", aseWS2016.getId().toString())
                        .param("studentId", student.getId().toString())
                        .param("authCode", totp.now())
                        .param("mark", "4")
                        .with(csrf())
        );

        // the grade should exist for the student
        Grade actualGradeStudent = gradeRepository.findAllOfStudent(student).get(0);
        assertEquals(lecturer1, actualGradeStudent.getLecturer());
        assertEquals(student, actualGradeStudent.getStudent());
        assertEquals(aseWS2016, actualGradeStudent.getCourse());
        assertEquals(4, actualGradeStudent.getMark().getMark());

        // and for the lecturer as well
        Grade actualGradeLecturer = gradeRepository.findByLecturerIdAndCourseId(lecturer1.getId(), aseWS2016.getId()).get(0);
        assertEquals(lecturer1, actualGradeLecturer.getLecturer());
        assertEquals(student, actualGradeLecturer.getStudent());
        assertEquals(aseWS2016, actualGradeLecturer.getCourse());
        assertEquals(4, actualGradeLecturer.getMark().getMark());
    }

    @Test
    public void issueGradeFailureWrongAuthCodeTest() throws Exception {

        // given "student" and "lecturer1" with the course "ase"

        // when "lecturer1" issues a grade for "student" with a wrong authentication code
        mockMvc.perform(
                post("/lecturer/course-details/addGrade")
                        .with(user("lecturer1").roles(Rolle.LECTURER.name()))
                        .param("courseId", aseWS2016.getId().toString())
                        .param("studentId", student.getId().toString())
                        .param("authCode", "-12345")
                        .param("mark", "4")
                        .with(csrf())
        );

        // the grade should not exist
        assertTrue(gradeRepository.findAllOfStudent(student).size() == 0);
        assertTrue(gradeRepository.findByLecturerIdAndCourseId(lecturer1.getId(), aseWS2016.getId()).size() == 0);
    }

}
