package at.ac.tuwien.inso.sqm.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.LecturerRepository;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.repository.StduentRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectRepository;
import at.ac.tuwien.inso.sqm.service.LecturerService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LecturerServiceSecurityTests {

    @Autowired
    private LecturerService lecturerService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SemestreRepository semesterRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private StduentRepository stduentRepository;

    private LecturerEntity lecturer;
    private Lehrveranstaltung course;
    private StudentEntity student;
    private UserAccountEntity user = new UserAccountEntity("lecturer1", "pass", Rolle.LECTURER);

    @BeforeTransaction
    public void beforeTransaction() {
        lecturer = lecturerRepository.save(new LecturerEntity("2", "LecturerEntity", "lecturer@lecturer.com", user));
    }

    @AfterTransaction
    public void afterTransaction() {
        lecturerRepository.delete(lecturer);
    }

    @Before
    public void setUp() {
        student = stduentRepository.save(new StudentEntity("1", "student1", "student@student.com"));
        Subjcet subject = subjectRepository.save(new Subjcet("ASE", BigDecimal.valueOf(6)));
        subject.addLecturers(lecturer);
        subjectRepository.save(subject);
        Semester semester = semesterRepository.save(new Semester(2016, SemestreTypeEnum.WinterSemester));
        course = courseRepository.save(new Lehrveranstaltung(subject, semester));
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void getLoggedInLecturerNotAuthenticated() {
        lecturerService.getLoggedInLecturer();
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "STUDENT")
    public void getLoggedInLecturerAuthenticatedAsStudent() {
        lecturerService.getLoggedInLecturer();
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "ADMIN")
    public void getLoggedInLecturerAuthenticatedAsAdmin() {
        lecturerService.getLoggedInLecturer();
    }

    @Test
    @WithUserDetails("lecturer1")
    public void getLoggedInLecturerAuthenticatedAsLecturer() {
        LecturerEntity result = lecturerService.getLoggedInLecturer();
        assertTrue(lecturer.equals(result));
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void getOwnSubjectsNotAuthenticated() {
        lecturerService.getOwnSubjects();
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "STUDENT")
    public void getOwnSubjectsAuthenticatedAsStudent() {
        lecturerService.getOwnSubjects();
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "ADMIN")
    public void getOwnSubjectsAuthenticatedAsAdmin() {
        lecturerService.getOwnSubjects();
    }

    @Test
    @WithUserDetails("lecturer1")
    public void getOwnSubjectsAuthenticatedAsLecturer() {
        Iterable<Subjcet> result = lecturerService.getOwnSubjects();
        assertNotNull(result);
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void findSubjectsForNotAuthenticated() {
        lecturerService.findSubjectsFor(lecturer);
    }

    @Test
    @WithMockUser
    public void findSubjectsForAuthenticated() {
        List<Subjcet> result = lecturerService.findSubjectsFor(lecturer);
        assertNotNull(result);
    }

}
