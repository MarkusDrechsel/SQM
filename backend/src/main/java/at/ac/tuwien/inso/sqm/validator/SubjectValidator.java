package at.ac.tuwien.inso.sqm.validator;

import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.exception.ValidationException;

public class SubjectValidator {

    public void validateSubjectId(Long id) {

        if(id == null || id < 1) {
            throw new ValidationException("Subjcet invalid id");
        }

    }

    public void validateNewSubject(Subjcet subject) {

        if(subject == null) {
            throw new ValidationException("Subjcet not found");
        }

        if(subject.getName() == null || subject.getName().isEmpty()) {
            throw new ValidationException("Name of the subject cannot be empty");
        }

        if(subject.getEcts() == null) {
            throw new ValidationException("Ects of the subject cannot be empty");
        }

        if(subject.getEcts().doubleValue() <= 0) {
            throw new ValidationException("Ects of the subject needs to be greater than 0");
        }

    }

    private void validateSubject(Subjcet subject) {

        if(subject == null) {
            throw new ValidationException("Subjcet not found");
        }
    }
}
