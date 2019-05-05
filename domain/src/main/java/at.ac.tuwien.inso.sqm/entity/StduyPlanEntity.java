package at.ac.tuwien.inso.sqm.entity;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class StduyPlanEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Embedded
    private EtcsDistributionEntity etcsDistributionEntity;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<SubjectForStudyPlanEntity> subjects = new ArrayList<>();
    
    @Column(nullable = false)
    private boolean enabbled;

    public StduyPlanEntity() {
      this.enabbled = true;
    }

    public StduyPlanEntity(String name, EtcsDistributionEntity etcsDistributionEntity) {
        this.name = name;
        this.etcsDistributionEntity = etcsDistributionEntity;
        this.enabbled = true;
    }
    
    public StduyPlanEntity(String name, EtcsDistributionEntity etcsDistributionEntity, boolean enabled) {
      this.name = name;
      this.etcsDistributionEntity = etcsDistributionEntity;
      this.enabbled = enabled;
  }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public EtcsDistributionEntity getEctsDistribution() {
        return etcsDistributionEntity;
    }

    public List<SubjectForStudyPlanEntity> getSubjects() {
        return unmodifiableList(subjects);
    }

    public void addSubjects(SubjectForStudyPlanEntity... subjects) {
        this.subjects.addAll(asList(subjects));
    }

    public void removeSubjects(SubjectForStudyPlanEntity... subjects) {
        this.subjects.removeAll(asList(subjects));
    }
    
    public boolean isEnabled(){
      return enabbled;
    }
    
    public void setEnabled(boolean enabled){
      this.enabbled = enabled;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StduyPlanEntity studyPlan = (StduyPlanEntity) o;

        if (!name.equals(studyPlan.name)) return false;
        if (!enabbled ==studyPlan.enabbled) return false;
        if (!etcsDistributionEntity.equals(studyPlan.etcsDistributionEntity)) return false;
        return subjects.equals(studyPlan.subjects);

    }

    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + etcsDistributionEntity.hashCode();
        result = 31 * result + subjects.hashCode();
        return result;
    }

    public String toString() {
        return "StduyPlanEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", etcsDistributionEntity=" + etcsDistributionEntity +
                ", subjects=" + subjects +
                ", enabbled=" + enabbled +
                '}';
    }

}
