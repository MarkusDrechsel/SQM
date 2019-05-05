package at.ac.tuwien.inso.sqm.service.course_recommendation; //FIXME package naming convention?!

import java.util.Map;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;

/**
 * Implementors of this interface have the responsibility of normalizing
 * the courses.
 */
public interface Normalizer {

    /**
     * Normalizes the course map so that only values between 0 and 1 are
     * obtained
     *
     * @param courses the courses to be normalized.
     */
    void normalize(Map<Lehrveranstaltung, Double> courses);
}
