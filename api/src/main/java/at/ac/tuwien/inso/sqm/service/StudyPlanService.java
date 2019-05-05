package at.ac.tuwien.inso.sqm.service;


import java.util.List;

import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.entity.StduyPlanEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import at.ac.tuwien.inso.sqm.entity.SubjectForStudyPlanEntity;
import at.ac.tuwien.inso.sqm.entity.SubjectWithGrade;

public interface StudyPlanService {

	/**
	 * creates a new study plan
	 * may throw a ValidationException if study plans name, or optional, mandatory or freechoice ects values are null or empty or <=0
	 *
	 * @param studyPlan
	 * @return
	 */
    @PreAuthorize("hasRole('ADMIN')")
    StduyPlanEntity create(StduyPlanEntity studyPlan);

    /**
     * returns a list of all StudyPlans.
     * user must be authorized
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    List<StduyPlanEntity> findAll();

    /**
     * returns the StduyPlanEntity with the corresponding id
     * may throw a BusinessObjectNotFoundException if there is no StduyPlanEntity with this id
     * @param id should not be null and not <1
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    StduyPlanEntity findOne(Long id);

    /**
     * try to find all SubjectsForStudyPlan by a study plan id.
     * should be ordered by semester recommendation
     * user must be authorized
     * 
     * @param id should not be null and not <1
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    List<SubjectForStudyPlanEntity> getSubjectsForStudyPlan(Long id);

    /**
     * 
     * returns a list of grades for the subjects for the CURRENTLY LOGGED IN STUDENT.
     * user needs to be authenticated
     * @param id should not be null and not <1
     * @return
     */
    @PreAuthorize("hasRole('STUDENT')")
    List<SubjectWithGrade> getSubjectsWithGradesForStudyPlan(Long id);

    /**
     * adds a subject to a study plan.
     * user needs to be ADMIN
     * 
     * @param subjectForStudyPlan should contain a subject that is not null and has a id that is not <1. also should contain a study plan that is not null and has an id that is not null and not <1
     */
    @PreAuthorize("hasRole('ADMIN')")
    void addSubjectToStudyPlan(SubjectForStudyPlanEntity subjectForStudyPlan);

    /**
     * returns all available subjects for the study plan with the id. the subjects can be filtered with the query string
     * the search strategy of the query should be byNameContainingIgnoreCase(query)
     * 
     * user has to be authenticated
     * 
     * @param id should not be null and not <1
     * @param query
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    List<Subjcet> getAvailableSubjectsForStudyPlan(Long id, String query);

    /**
     * disables the study plan of the given id.
     * 
     * user needs role ADMIN
     * may throw BusinessObjectNotFoundException if the study plan with this id does not exists
     * may throw a ValidationException if the id is not correct
     * 
     * @param id should not be null and not <1
     */
    @PreAuthorize("hasRole('ADMIN')")
    StduyPlanEntity disableStudyPlan(Long id);

	/**
	 * removes a given subject s from the study plan sp
	 * user need role ADMIN
	 * 
	 *
	 * @param sp should not be null and the sp.id should not be <1 and not null
	 * @param s should have an id
	 */
	@PreAuthorize("hasRole('ADMIN')")
    void removeSubjectFromStudyPlan(StduyPlanEntity sp, Subjcet s);
}
