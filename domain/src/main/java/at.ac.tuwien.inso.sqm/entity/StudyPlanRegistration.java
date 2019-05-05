package at.ac.tuwien.inso.sqm.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import at.ac.tuwien.inso.sqm.dto.StudyPlanRegistrationDto;

@Entity
public class StudyPlanRegistration {

    @Id
    @GeneratedValue
    private Long Id;

    @ManyToOne(optional = false)
    private StduyPlanEntity studyplan;

    @ManyToOne(optional = false)
    private Semester registeredSince;

    protected StudyPlanRegistration() {

    }

    public StudyPlanRegistration(StduyPlanEntity studyplan, Semester registeredSince) {
        this.studyplan = studyplan;
        this.registeredSince = registeredSince;
    }

    public Long getId() {
        return Id;
    }

    public StduyPlanEntity getStudyplan() {
        return studyplan;
    }

    public Semester getRegisteredSince() {
        return registeredSince;
    }

    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudyPlanRegistration that = (StudyPlanRegistration) o;

        if (Id != null ? !Id.equals(that.Id) : that.Id != null) return false;
        if (studyplan != null ? !studyplan.equals(that.studyplan) : that.studyplan != null) return false;
        return registeredSince != null ? registeredSince.equals(that.registeredSince) : that.registeredSince == null;

    }
    
    public StudyPlanRegistrationDto toDto(){
    	StudyPlanRegistrationDto dto = new StudyPlanRegistrationDto();
    	dto.registeredSince = registeredSince.toDto();
    	dto.stduyplan = studyplan;
    	return dto;
    	
    }

    public int hashCode() {
        int result = Id != null ? Id.hashCode() : 0;
        result = 31 * result + (studyplan != null ? studyplan.hashCode() : 0);
        result = 31 * result + (registeredSince != null ? registeredSince.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "StudyPlanRegistration{" +
                "Id=" + Id +
                ", stduyplan=" + studyplan +
                ", registeredSince=" + registeredSince +
                '}';
    }
}
