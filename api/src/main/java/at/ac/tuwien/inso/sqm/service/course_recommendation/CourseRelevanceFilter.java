package at.ac.tuwien.inso.sqm.service.course_recommendation; //FIXME package naming convention?!

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;

/**
 * Implementors of this interface have the responsibility of filtering out
 * courses which are from the start not fit to be recommended to the student.
 */
public interface CourseRelevanceFilter {

    /**
     * Filters courses by an implementation specific relevance criteria.
     *
     * @param courses the courses to be filtered.
     * @param student the student for whom the relevance of the courses is relatively calculated.
     * @return the courses which met the relevance criteria for the student. It is guaranteed that
     * the result is a subset of the list of courses given as input.
     */
    List<Lehrveranstaltung> filter(List<Lehrveranstaltung> courses, StudentEntity student);
}
