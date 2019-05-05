package at.ac.tuwien.inso.sqm.security;

import at.ac.tuwien.inso.sqm.dto.AddCourseForm;
import at.ac.tuwien.inso.sqm.entity.*;
import at.ac.tuwien.inso.sqm.exception.BusinessObjectNotFoundException;
import at.ac.tuwien.inso.sqm.exception.ValidationException;
import at.ac.tuwien.inso.sqm.repository.*;
import at.ac.tuwien.inso.sqm.service.LehrveranstaltungServiceInterface;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CourseServiceSecurityTests {

    @Autowired
    private LehrveranstaltungServiceInterface courseService;

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
    private StudentEntity adminaccount;
    private UserAccountEntity user = new UserAccountEntity("student1", "pass", Rolle.STUDENT);
    private UserAccountEntity admin = new UserAccountEntity("admin1", "pass", Rolle.ADMIN);
    private UserAccountEntity lecturerRole = new UserAccountEntity("LecturerEntity", "pass", Rolle.LECTURER);

    @BeforeTransaction
    public void beforeTransaction() {
        student = stduentRepository.save(new StudentEntity("1", "student1", "student@student.com", user));
        //workaround because integrationtest is not possible to instanceise an admin
        adminaccount = stduentRepository.save(new StudentEntity("2", "admin1", "admin@test.com", admin));
    }

    @AfterTransaction
    public void afterTransaction() {
        stduentRepository.delete(student);
        stduentRepository.delete(adminaccount);
    }

    @Before
    public void setUp() {
        lecturer = lecturerRepository.save(new LecturerEntity("2", "LecturerEntity", "lecturer@lecturer.com", lecturerRole));
        Subjcet subject = subjectRepository.save(new Subjcet("ASE", BigDecimal.valueOf(6)));
        subject.addLecturers(lecturer);
        subjectRepository.save(subject);
        Semester semester = semesterRepository.save(new Semester(2016, SemestreTypeEnum.WinterSemester));
        course = courseRepository.save(new Lehrveranstaltung(subject, semester));
        course.setStudentLimits(0);
        course.addStudents(student);
    }

    /*@After
    public void destroy(){
    	course.removeStudents(student);
    	courseRepository.save(course);
    }*/

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void findCourseForCurrentSemesterWithNameNotAuthenticated() {
        courseService.findCourseForCurrentSemesterWithName("test", new PageRequest(1, 1));
    }

//    @Test
//    @WithMockUser
//    public void findCourseForCurrentSemesterWithNameAuthenticated() {
//        Page<Lehrveranstaltung> results = courseService.findCourseForCurrentSemesterWithName("ASE", new PageRequest(0, 1));
//        assertFalse(results.getContent().size() == 0);
//        assertEquals(results.getContent().get(0).getSubject().getName(), "ASE");
//    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void findCoursesForCurrentSemesterForLecturerNotAuthenticated() {
        courseService.findCoursesForCurrentSemesterForLecturer(lecturer);
    }

//    @Test
//    @WithMockUser
//    public void findCoursesForCurrentSemesterForLecturerAuthenticated() {
//        List<Lehrveranstaltung> courses = courseService.findCoursesForCurrentSemesterForLecturer(lecturer);
//        assertTrue(courses.size() == 1);
//    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void saveCourseNotAuthenticated() {
        AddCourseForm addCourseForm = new AddCourseForm(course);
        courseService.saveCourse(addCourseForm);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(username = "student1", roles = "STUDENT")
    public void saveCourseAuthenticatedAsStudent() {
        AddCourseForm addCourseForm = new AddCourseForm(course);
        courseService.saveCourse(addCourseForm);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username="admin1")
    public void saveCourseAuthenticatedAsAdmin() {
        AddCourseForm addCourseForm = new AddCourseForm(course);
        Lehrveranstaltung result = courseService.saveCourse(addCourseForm);
        assertTrue(addCourseForm.getCourse().equals(result));
    }

    @Test
    @WithMockUser(roles = "LECTURER", username="LecturerEntity")
    public void saveCourseAuthenticatedAsLecturer() {
        AddCourseForm addCourseForm = new AddCourseForm(course);
        Lehrveranstaltung result = courseService.saveCourse(addCourseForm);
        assertTrue(addCourseForm.getCourse().equals(result));
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void findOneNotAuthenticated() {
        courseService.findeLehrveranstaltung(Long.parseLong("1"));
    }

    @Test
    @WithMockUser
    public void findOneAuthenticated() {
        Lehrveranstaltung result = courseService.findeLehrveranstaltung(course.getId());
        assertTrue(course.getId().equals(result.getId()));
    }
    

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void registerStudentForCourseNotAuthenticated() {
        courseService.studentZurLehrveranstaltungAnmelden(course);
    }
    
    @Test(expected = ValidationException.class)
    @WithMockUser(roles = "LECTURER", username="LecturerEntity")
    public void removeCourseAuthenticatedButHasStudents(){
    	AddCourseForm addCourseForm = new AddCourseForm(course);
        Lehrveranstaltung result = courseService.saveCourse(addCourseForm);
        assertTrue(addCourseForm.getCourse().equals(result));
    	
        courseService.remove(result.getId());
        Lehrveranstaltung result2 = courseService.findeLehrveranstaltung(course.getId());
    }
    
    @Test(expected = BusinessObjectNotFoundException.class)
    @WithMockUser(roles = "LECTURER", username="LecturerEntity")
    public void removeCourseAuthenticated(){
    	course.removeStudents(student);
    	courseRepository.save(course);
    	AddCourseForm addCourseForm = new AddCourseForm(course);
        Lehrveranstaltung result = courseService.saveCourse(addCourseForm);
        assertTrue(addCourseForm.getCourse().equals(result));
    	
        courseService.remove(result.getId());
        Lehrveranstaltung result2 = courseService.findeLehrveranstaltung(course.getId());
    }
    
    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void removeCourseNotAuthenticated(){
    	courseService.remove(course.getId());
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "ADMIN")
    public void registerStudentForCourseAuthenticatedAsAdmin() {
        courseService.studentZurLehrveranstaltungAnmelden(course);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "LECTURER")
    public void registerStudentForCourseAuthenticatedAsLecturer() {
        courseService.studentZurLehrveranstaltungAnmelden(course);
    }

    @Test
    @WithUserDetails(value = "student1")
    public void registerStudentForCourseAuthenticatedAsStudent() {
        assertFalse(courseService.studentZurLehrveranstaltungAnmelden(course));
    }

}
