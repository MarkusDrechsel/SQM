package at.ac.tuwien.inso.sqm.integrationtest;

import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.entity.Semester;
import at.ac.tuwien.inso.sqm.entity.SemestreTypeEnum;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectRepository;
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

import java.math.BigDecimal;

import static java.util.Arrays.asList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class StudentCoursesForSemesterAndSubjectTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SemestreRepository semesterRepository;

    private Subjcet subject = new Subjcet("maths", new BigDecimal(6.0));
    private Semester ws2016 = new Semester(2016, SemestreTypeEnum.WinterSemester);

    @Before
    public void setUp() {
        subjectRepository.save(subject);
        semesterRepository.save(ws2016);
    }

    @Test
    public void getTheOnlyCourseOfCurrentSemesterForSubjectTest() throws Exception {

        // given only one course for the ws2016 of the subject "maths"
        Lehrveranstaltung course = courseRepository.save(new Lehrveranstaltung(subject, ws2016));

        // the user should be redirected directly to the course page
        mockMvc.perform(
                get("/student/lehrveranstaltungen/semester/subject")
                        .with(user("student").roles("STUDENT"))
                        .param("subjectId", subject.getId().toString())
        ).andExpect(
                redirectedUrl("/student/lehrveranstaltungen/" + course.getId())
        );
    }

    @Test
    public void getManyCoursesOfCurrentSemesterForSubjectTest() throws Exception {

        // given two courses for the ws2016 of the subject "maths"
        Lehrveranstaltung course1 = courseRepository.save(new Lehrveranstaltung(subject, ws2016));
        Lehrveranstaltung course2 = courseRepository.save(new Lehrveranstaltung(subject, ws2016));

        // the user should see a list of courses of this subject
        mockMvc.perform(
                get("/student/lehrveranstaltungen/semester/subject")
                        .with(user("student").roles("STUDENT"))
                        .param("subjectId", subject.getId().toString())
        ).andExpect(
                view().name("/student/courses-for-subject")
        ).andExpect(
                model().attribute("coursesForSemesterAndSubject", asList(course1, course2))
        );
    }

}
