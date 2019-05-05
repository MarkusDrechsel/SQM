package at.ac.tuwien.inso.sqm.service;

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.StduyPlanEntity;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.entity.UserAccountEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import at.ac.tuwien.inso.sqm.dto.SemesterDto;
import at.ac.tuwien.inso.sqm.entity.StudyPlanRegistration;

public interface StudentServiceInterface {

	/**
	 * returns one student by id if he exists 
	 * id should not be null and not <1
	 * 
	 * @param id
	 * @return
	 */
    @PreAuthorize("isAuthenticated()")
    StudentEntity findOne(Long id);;

    /**
     * returns one student by account
     * user needs to be authenticated
     * @param account
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    StudentEntity findOne(UserAccountEntity account);

    /**
     * returns a student by its username
     * user needs to be authenticated
     * @param username
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    StudentEntity findByUsername(String username);

    /**
     * returns all studyplanregistrations for the student. student should not be null!
     * user needs to be admin
     * @param student
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    List<StudyPlanRegistration> findStudyPlanRegistrationsFor(StudentEntity student);

    /**
     * registers a student to a stduyplan for the current semester. student and stduyplan should not be null
     * may start a new semester
     * 
     * user needs to be admin
     * 
     * @param student
     * @param studyPlan
     */
    @PreAuthorize("hasRole('ADMIN')")
    void registerStudentToStudyPlan(StudentEntity student, StduyPlanEntity studyPlan);

    /**
     * registers a student to a stduyplan for the given semester
     * student and stduyplan should not be null
     * 
     * user needs to be admin!
     * 
     * @param student
     * @param studyPlan
     * @param currentSemester
     */
    @PreAuthorize("hasRole('ADMIN')")
    void registerStudentToStudyPlan(StudentEntity student, StduyPlanEntity studyPlan, SemesterDto currentSemester);
}
