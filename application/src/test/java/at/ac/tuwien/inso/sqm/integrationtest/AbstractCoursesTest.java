package at.ac.tuwien.inso.sqm.integrationtest;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.LecturerRepository;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.repository.StduentRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectRepository;
import at.ac.tuwien.inso.sqm.repository.TagRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractCoursesTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected CourseRepository courseRepository;

    @Autowired
    protected TagRepository tagRepository;

    @Autowired
    private SemestreRepository semesterRepository;

    @Autowired
    protected SubjectRepository subjectRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    protected StduentRepository stduentRepository;

    protected UserAccountEntity user1 = new UserAccountEntity("lecturer1", "pass", Rolle.LECTURER);
    protected LecturerEntity lecturer1 = new LecturerEntity("l0001", "LecturerEntity 1", "email1@uis.at", user1);
    protected LecturerEntity lecturer2 = new LecturerEntity("l0002", "LecturerEntity 2", "email2@uis.at", new UserAccountEntity("lecturer2", "pass", Rolle.LECTURER));
    protected LecturerEntity lecturer3 = new LecturerEntity("l0003", "LecturerEntity 3", "email3@uis.at", new UserAccountEntity("lecturer3", "pass", Rolle.LECTURER));
    protected UserAccountEntity studentUserAccount = new UserAccountEntity("student", "pass", Rolle.STUDENT);
    protected StudentEntity student = new StudentEntity("s1234", "StudentEntity", "student@uis.at", studentUserAccount);
    protected Semester ss2016 = new Semester(2016, SemestreTypeEnum.SummerSemester);
    protected Semester ws2016 = new Semester(2016, SemestreTypeEnum.WinterSemester);
    protected Semester ss2017 = new Semester(2017, SemestreTypeEnum.SummerSemester);
    protected Semester ws2017 = new Semester(2017, SemestreTypeEnum.WinterSemester);
    protected Semester ss2018 = new Semester(2018, SemestreTypeEnum.SummerSemester);
    protected Semester ss2019 = new Semester(2019, SemestreTypeEnum.SummerSemester);
    protected Subjcet calculus = new Subjcet("Calculus", new BigDecimal(3.0));
    protected Subjcet sepm = new Subjcet("SEPM", new BigDecimal(6.0));
    protected Subjcet ase = new Subjcet("ASE", new BigDecimal(6.0)).addLecturers(lecturer3);
    protected Lehrveranstaltung sepmSS2016 = new Lehrveranstaltung(sepm, ss2016);
    protected Lehrveranstaltung sepmSS2019 = new Lehrveranstaltung(sepm, ss2019);
    protected Lehrveranstaltung aseSS2019 = new Lehrveranstaltung(ase, ss2019);
    protected Lehrveranstaltung calculusSS2019 = new Lehrveranstaltung(calculus, ss2019);
    protected Tag tag1 = new Tag("Computer Science");
    protected Tag tag2 = new Tag("Math");
    protected Tag tag3 = new Tag("Fun");
    protected Tag tag4 = new Tag("Easy");
    protected Tag tag5 = new Tag("Difficult");

    protected List<Lehrveranstaltung> expectedCourses;
    protected List<Lehrveranstaltung> expectedCoursesForLecturer1;
    protected List<Lehrveranstaltung> expectedCoursesForLecturer2;
    protected List<Lehrveranstaltung> expectedCoursesForLecturer3;

    @Before
    public void setUp() {
        student = stduentRepository.save(student);

        lecturerRepository.save(lecturer1);
        lecturerRepository.save(lecturer2);
        lecturerRepository.save(lecturer3);

        semesterRepository.save(ss2016);
        semesterRepository.save(ws2016);
        semesterRepository.save(ss2017);
        semesterRepository.save(ws2017);
        semesterRepository.save(ss2018);
        semesterRepository.save(ss2019);

        subjectRepository.save(calculus);
        calculus.addLecturers(lecturer3);
        subjectRepository.save(sepm);
        sepm.addLecturers(lecturer1);
        subjectRepository.save(ase);
        ase.addLecturers(lecturer1, lecturer2);

        calculusSS2019.addStudents(student);

        sepmSS2016 = courseRepository.save(sepmSS2016);
        sepmSS2019 = courseRepository.save(sepmSS2019);
        aseSS2019 = courseRepository.save(aseSS2019);
        calculusSS2019 = courseRepository.save(calculusSS2019);

        expectedCourses = Arrays.asList(sepmSS2019, aseSS2019, calculusSS2019);
        expectedCoursesForLecturer1 = Arrays.asList(sepmSS2019, aseSS2019);
        expectedCoursesForLecturer3 = Arrays.asList(aseSS2019);
        expectedCoursesForLecturer2 = Arrays.asList(calculusSS2019);

        tagRepository.save(asList(tag1, tag2, tag3, tag4, tag5));
    }


}
