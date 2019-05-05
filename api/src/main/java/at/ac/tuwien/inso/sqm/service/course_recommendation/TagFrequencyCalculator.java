package at.ac.tuwien.inso.sqm.service.course_recommendation; //FIXME package naming convention?!

import java.util.Map;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.entity.Tag;

public interface TagFrequencyCalculator {

    /**
     * Berechnet die Häufigkeit wie oft ein Tag auftritt. Mit besonderer Unterscheidung zwischen den Lehrveranstaltungen
     * bei der ein Student Feedback gegeben hat, Lehrveranstaltungen für die der Student eine Benotung erhalten hat und
     * allen anderen Lehrveranstaltungen.
     *
     * @param student der Student, für den die Berechnung durchgeführt wird
     * @return eine Map mit den Tags und deren Häufigkeit, mit der sie auftreten
     */
    Map<Tag, Double> calculate(StudentEntity student);
}
