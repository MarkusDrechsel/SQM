package at.ac.tuwien.inso.sqm.validator;

import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.entity.StduyPlanEntity;
import at.ac.tuwien.inso.sqm.entity.SubjectForStudyPlanEntity;
import at.ac.tuwien.inso.sqm.exception.ValidationException;

public class StudyPlanValidator {

    public void validateNewStudyPlan(StduyPlanEntity studyPlan) {

        validateStudyPlan(studyPlan);

        if(studyPlan.getName() == null || studyPlan.getName().isEmpty()) {
            throw new ValidationException("Name of the study plan cannot be empty");
        }

        if(studyPlan.getEctsDistribution().getMandatory() == null) {
            throw new ValidationException("Mandatory ects of the study plan cannot be empty");
        }

        if(studyPlan.getEctsDistribution().getMandatory().doubleValue() <= 0) {
            throw new ValidationException("Mandatory ects of the study plan needs to be greater than 0");
        }

        if(studyPlan.getEctsDistribution().getOptional() == null) {
            throw new ValidationException("Optional ects of the study plan cannot be empty");
        }

        if(studyPlan.getEctsDistribution().getOptional().doubleValue() <= 0) {
            throw new ValidationException("Optional ects of the study plan needs to be greater than 0");
        }

        if(studyPlan.getEctsDistribution().getFreeChoice() == null) {
            throw new ValidationException("Free choice ects of the study plan cannot be empty");
        }

        if(studyPlan.getEctsDistribution().getFreeChoice().doubleValue() <= 0) {
            throw new ValidationException("Free choice ects of the study plan needs to be greater than 0");
        }

    }

    private void validateStudyPlan(StduyPlanEntity studyPlan) {

        if(studyPlan == null) {
            throw new ValidationException("Study plan not found");
        }

    }

    public void validateStudyPlanId(Long id) {

        if(id == null || id < 1) {
            throw new ValidationException("Study plan invalid id");
        }

    }

    public void validateNewSubjectForStudyPlan(SubjectForStudyPlanEntity subjectForStudyPlan) {

        if(subjectForStudyPlan == null) {
            throw new ValidationException("No subject found to add");
        }

        if(subjectForStudyPlan.getSubject() == null) {
            throw new ValidationException("Subjcet not found");
        }

        if(subjectForStudyPlan.getSubject().getId() == null || subjectForStudyPlan.getSubject().getId().intValue() < 1) {
            throw new ValidationException("Subjcet invalid id");
        }

        validateStudyPlan(subjectForStudyPlan.getStudyPlan());
        validateStudyPlanId(subjectForStudyPlan.getStudyPlan().getId());

    }

    public void validateRemovingSubjectFromStudyPlan(StduyPlanEntity studyPlan, Subjcet subject) {

        if(subject == null) {
            throw new ValidationException("Subjcet not found");
        }

        if(subject.getId() == null || subject.getId().intValue() < 1) {
            throw new ValidationException("Subjcet invalid id");
        }

        validateStudyPlan(studyPlan);
        validateStudyPlanId(studyPlan.getId());
    }
}
