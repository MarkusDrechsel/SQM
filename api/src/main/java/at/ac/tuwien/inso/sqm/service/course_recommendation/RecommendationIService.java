package at.ac.tuwien.inso.sqm.service.course_recommendation; //FIXME package naming convention?!

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;

public interface RecommendationIService {

    /**
     * Recommends courses for a student by first filtering courses, running all the course scorers,
     * normalizing the results and scaling each scorer function by weight. Finally, the list is
     * sorted by score.
     *
     * The user needs to be authenticated.
     *
     * @param student The student that needs recommendations
     * @return a sorted list of courses by weighted score
     */
    @PreAuthorize("isAuthenticated()")
    List<Lehrveranstaltung> recommendCourses(StudentEntity student);

    /**
     * Recommends courses, whereby the list contains no more than a number of entries defined as a
     * constant in the implementation.
     *
     * The user needs to be authenticated.
     *
     * @param student
     * @return A sorted list of courses
     */
    @PreAuthorize("isAuthenticated()")
    List<Lehrveranstaltung> recommendCoursesSublist(StudentEntity student);
}
