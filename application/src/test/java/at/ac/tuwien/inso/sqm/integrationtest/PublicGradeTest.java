package at.ac.tuwien.inso.sqm.integrationtest;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.GradeRepository;
import at.ac.tuwien.inso.sqm.repository.LecturerRepository;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.repository.StduentRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PublicGradeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SemestreRepository semesterRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private StduentRepository stduentRepository;

    private Grade grade;

    @Before
    public void setUp() {

        Subjcet subject = new Subjcet("Test", new BigDecimal(3));
        subjectRepository.save(subject);

        Semester semester = new Semester(2017, SemestreTypeEnum.SummerSemester);
        semesterRepository.save(semester);

        Lehrveranstaltung course = new Lehrveranstaltung(subject, semester);
        courseRepository.save(course);

        LecturerEntity lecturer = new LecturerEntity("123", "TestLecturer", "test@lecturer.com");
        lecturerRepository.save(lecturer);

        StudentEntity student = new StudentEntity("456", "TestStudent", "test@student.com");
        stduentRepository.save(student);

        grade = gradeRepository.save(new Grade(course, lecturer, student, MarkEntity.EXCELLENT));
        grade.getId();
    }

    @Test
    public void generateGradePDFTest() throws Exception {
        grade = gradeRepository.findOne(grade.getId());
        mockMvc.perform(
                get("/public/grade?identifier=" + grade.getUrlIdentifier())
                        .with(anonymous())
                        .accept(MediaType.APPLICATION_PDF)
        ).andExpect(
                model().attribute("grade", grade)
        );
    }
}
