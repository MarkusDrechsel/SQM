package at.ac.tuwien.inso.sqm.validator;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.exception.ValidationException;

public class CourseValidator {

    public void validateNewCourse(Lehrveranstaltung course) {

        validateCourse(course);

        if(course.getStudentLimits() < 0) {
            throw new ValidationException("The student limit of the course needs to be at least 0");
        }

    }

    public void validateCourse(Lehrveranstaltung course) {

        if(course == null || course.getSubject() == null) {
            throw new ValidationException("Lehrveranstaltung not found");
        }

    }

    public void validateCourseId(Long id) {

        if(id == null || id < 1) {
            throw new ValidationException("Lehrveranstaltung invalid id");
        }

    }

    public void validateStudent(StudentEntity student) {

        if(student == null) {
            throw new ValidationException("StudentEntity not found");
        }

    }
}
