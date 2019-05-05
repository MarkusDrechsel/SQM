package at.ac.tuwien.inso.sqm.service;

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.MarkEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import at.ac.tuwien.inso.sqm.dto.GradeAuhtorizationDTO;
import at.ac.tuwien.inso.sqm.entity.Grade;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;

@Service
public interface GradeIService {

	/**
	 * returns a default GradeAuthroizationDto. Default means that the grade is failed = MarkEntity.FAILED.
	 * if no logged in lecturer, no student with the id, no course can be found, a BusinessObjectNotFoundException will be thrown
	 * if student is not registered for the course, a ValidationException will be thrown
	 * 
	 * @param studentId
	 * @param courseId
	 * @return
	 */
    @PreAuthorize("hasRole('LECTURER')")
    GradeAuhtorizationDTO getDefaultGradeAuthorizationDTOForStudentAndCourse(Long studentId, Long courseId);

    
    /**
     * saves a new grade for student for a course.
     * 
     * the lecturer of the gradeauthrizationDTO needs to be equal to the currently logged in lecturer. otherwise a ValidationException is thrown
     * further, lecturer needs to have a two factor authentication value set. otherwise a ValidationException will be thrown
     * the authentication code value needs to be a number. otherwise a ValidationException is thrown
     * 
     * @param grade
     * @return
     */
    @PreAuthorize("hasRole('LECTURER')")
    Grade saveNewGradeForStudentAndCourse(GradeAuhtorizationDTO grade);

    /**
     * returns a list of grades for the given course  of the currently logged in lecturer
     * 
     * user needs to be lecturer
     * 
     * @param courseId
     * @return
     */
    @PreAuthorize("hasRole('LECTURER')")
    List<Grade> getGradesForCourseOfLoggedInLecturer(Long courseId);
    
    /**
     * returns a list of grades for the given courseID
     * user needs to be lecturer
     * @param courseId should not be null and not <1
     * @return
     */
    @PreAuthorize("hasRole('LECTURER')")
	List<Grade> findAllByCourseId(Long courseId);

    /**
     * returns all grades of the currently logged in student
     * 
     * user needs to be student
     * @return
     */
    @PreAuthorize("hasRole('STUDENT')")
    List<Grade> getGradesForLoggedInStudent();

    /**
     * returns the grade for a string identifier for validation purposes
     * 
     * user needs not authentication
     * 
     * @param identifier
     * @return
     */
    Grade getForValidation(String identifier);

    /**
     * returns all grades for a given student
     * 
     * @param student should not be null
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    List<Grade> findAllOfStudent(StudentEntity student);

    /**
     * returns an list of MarkEntity options (MarkEntity is an enumeration)
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    List<MarkEntity> getMarkOptions();

}
