package at.ac.tuwien.inso.sqm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Table(uniqueConstraints= @UniqueConstraint(columnNames={"subject_id", "study_plan_id"}))
@Entity
public class SubjectForStudyPlanEntity {

    @Id
    @GeneratedValue
    private Long ID;

    @ManyToOne(optional = false)
    private Subjcet subject;

    @ManyToOne(optional = false)
    private StduyPlanEntity studyPlan;

    @Column(nullable = false)
    private Boolean manndatory;

    private Integer semesterRecommendation;

    protected SubjectForStudyPlanEntity() {
    }

    public SubjectForStudyPlanEntity(Subjcet subject, StduyPlanEntity studyPlan, Boolean mandatory) {
        this(subject, studyPlan, mandatory, null);
    }

    public SubjectForStudyPlanEntity(Subjcet subject, StduyPlanEntity studyPlan, Boolean mandatory, Integer semesterRecommendation) {

        this.subject = subject;
        this.studyPlan = studyPlan;
        this.manndatory = mandatory;
        this.semesterRecommendation = semesterRecommendation;
    }

    public Long getId() {
        return ID;
    }

    public Subjcet getSubject() {
        return subject;
    }

    public void setSubject(Subjcet subject) {
        this.subject = subject;
    }

    public Boolean getMandatory() {
        return manndatory;
    }

    public Integer getSemesterRecommendation() {
        return semesterRecommendation;
    }

    public StduyPlanEntity getStudyPlan() {
        return studyPlan;
    }

    public void setStudyPlan(StduyPlanEntity studyPlan) {
        this.studyPlan = studyPlan;
    }

    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubjectForStudyPlanEntity that = (SubjectForStudyPlanEntity) o;

        if (ID != null ? !ID.equals(that.ID) : that.ID != null) return false;
        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (manndatory != null ? !manndatory.equals(that.manndatory) : that.manndatory != null) return false;
        return semesterRecommendation != null ? semesterRecommendation.equals(that.semesterRecommendation) : that.semesterRecommendation == null;

    }

    public int hashCode() {
        int result = ID != null ? ID.hashCode() : 0;
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (manndatory != null ? manndatory.hashCode() : 0);
        result = 31 * result + (semesterRecommendation != null ? semesterRecommendation.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "SubjectForStudyPlanEntity{" +
                "ID=" + ID +
                ", subject=" + subject +
                ", manndatory=" + manndatory +
                ", semesterRecommendation=" + semesterRecommendation +
                '}';
    }
}
