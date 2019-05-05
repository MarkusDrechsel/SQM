package at.ac.tuwien.inso.sqm.security;

import java.math.BigDecimal;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.dto.SemesterDto;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.repository.StudyPlanRepository;
import at.ac.tuwien.inso.sqm.service.StudentServiceInterface;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StudentServiceSecurityTests {

    @Autowired
    private StudentServiceInterface studentService;

    @Autowired
    private SemestreRepository semesterRepository;

    @Autowired
    private StudyPlanRepository studyPlanRepository;

    private SemesterDto semester;
    private StduyPlanEntity studyPlan;

    @Before
    public void setUp() {
        Semester localSemester = new Semester(2016, SemestreTypeEnum.WinterSemester);

        semester = semesterRepository.save(localSemester).toDto();

        StduyPlanEntity localStudyPlan = new StduyPlanEntity("TestStudyPlan",
                new EtcsDistributionEntity(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));

        studyPlan = studyPlanRepository.save(localStudyPlan);
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void findOneNotAuthenticated() {
        studentService.findOne(Long.valueOf(1));
    }

    @Test
    @WithMockUser
    public void findOneAuthenticated() {
        studentService.findOne(Long.valueOf(1));
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void registerStudentToStudyPlanNotAuthenticated() {
        studentService.registerStudentToStudyPlan(
                new StudentEntity("student", "student", "student@student.com"),
                studyPlan);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "STUDENT")
    public void registerStudentToStudyPlanAuthenticatedAsStudent() {
        studentService.registerStudentToStudyPlan(
                new StudentEntity("student", "student", "student@student.com"),
                studyPlan);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "LECTURER")
    public void registerStudentToStudyPlanAuthenticatedAsLecturer() {
        studentService.registerStudentToStudyPlan(
                new StudentEntity("student", "student", "student@student.com"),
                studyPlan);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void registerStudentToStudyPlanAuthenticatedAsAdmin() {
        studentService.registerStudentToStudyPlan(
                new StudentEntity("student", "student", "student@student.com"),
                studyPlan);
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void registerStudentToStudyPlanVersion2NotAuthenticated() {
        studentService.registerStudentToStudyPlan(
                new StudentEntity("student", "student", "student@student.com"),
                studyPlan,
                semester);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "STUDENT")
    public void registerStudentToStudyPlanVersion2AuthenticatedAsStudent() {
        studentService.registerStudentToStudyPlan(
                new StudentEntity("student", "student", "student@student.com"),
                studyPlan,
                semester);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "LECTURER")
    public void registerStudentToStudyPlanVersion2AuthenticatedAsLecturer() {
        studentService.registerStudentToStudyPlan(
                new StudentEntity("student", "student", "student@student.com"),
                studyPlan,
                semester);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void registerStudentToStudyPlanVersion2AuthenticatedAsAdmin() {
        studentService.registerStudentToStudyPlan(
                new StudentEntity("student", "student", "student@student.com"),
                studyPlan,
                semester);
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void findStudyPlanRegistrationsForNotAuthenticated() {
        studentService.findStudyPlanRegistrationsFor(
                new StudentEntity("student", "student", "student@student.com"));
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "STUDENT")
    public void findStudyPlanRegistrationsForAuthenticatedAsStudent() {
        studentService.findStudyPlanRegistrationsFor(
                new StudentEntity("student", "student", "student@student.com"));
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "LECTURER")
    public void findStudyPlanRegistrationsForAuthenticatedAsLecturer() {
        studentService.findStudyPlanRegistrationsFor(
                new StudentEntity("student", "student", "student@student.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findStudyPlanRegistrationsForAuthenticatedAsAdmin() {
        studentService.findStudyPlanRegistrationsFor(
                new StudentEntity("student", "student", "student@student.com"));
    }

}
