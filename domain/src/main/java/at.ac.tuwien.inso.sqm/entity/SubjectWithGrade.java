package at.ac.tuwien.inso.sqm.entity;

/**
 * Note: this is not an entity, just a wrapper.
 */
public class SubjectWithGrade {

    private SubjectForStudyPlanEntity subjectForStduyPlan;
    private Grade grade;
    private SubjectTyppe subejctType;

    public SubjectWithGrade(SubjectForStudyPlanEntity subjectForStudyPlan, Grade grade, SubjectTyppe subjectType) {
        this.subjectForStduyPlan = subjectForStudyPlan;
        this.grade = grade;
        this.subejctType = subjectType;
    }

    public SubjectWithGrade(SubjectForStudyPlanEntity subjectForStudyPlan, SubjectTyppe subjectType) {
        this.subjectForStduyPlan = subjectForStudyPlan;
        this.subejctType = subjectType;
    }

    public SubjectWithGrade(Grade grade, SubjectTyppe subjectType) {
        this.grade = grade;
        this.subejctType = subjectType;
    }

    public SubjectForStudyPlanEntity getSubjectForStudyPlan() {
        return subjectForStduyPlan;
    }

    public void setSubjectForStudyPlan(SubjectForStudyPlanEntity subjectForStudyPlan) {
        this.subjectForStduyPlan = subjectForStudyPlan;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public SubjectTyppe getSubjectType() {
        return subejctType;
    }

    public void setSubjectType(SubjectTyppe subjectType) {
        this.subejctType = subjectType;
    }

    public String toString() {
        return "SubjectWithGrade{" +
                "subjectForStduyPlan=" + subjectForStduyPlan +
                ", grade=" + grade +
                ", subejctType=" + subejctType +
                '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubjectWithGrade that = (SubjectWithGrade) o;

        if (subjectForStduyPlan != null ? !subjectForStduyPlan.equals(that.subjectForStduyPlan) : that.subjectForStduyPlan != null)
            return false;
        if (grade != null ? !grade.equals(that.grade) : that.grade != null) return false;
        return subejctType == that.subejctType;

    }

    public int hashCode() {
        int result = subjectForStduyPlan != null ? subjectForStduyPlan.hashCode() : 0;
        result = 31 * result + (grade != null ? grade.hashCode() : 0);
        result = 31 * result + subejctType.hashCode();
        return result;
    }
}
