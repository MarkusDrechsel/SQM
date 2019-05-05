package at.ac.tuwien.inso.sqm.repository;

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.entity.StduyPlanEntity;
import at.ac.tuwien.inso.sqm.entity.SubjectForStudyPlanEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectForStudyPlanRepository extends CrudRepository<SubjectForStudyPlanEntity, Long> {

    List<SubjectForStudyPlanEntity> findByStudyPlanIdOrderBySemesterRecommendation(Long id);

    List<SubjectForStudyPlanEntity> findBySubject(Subjcet subject);

    List<SubjectForStudyPlanEntity> findBySubjectInAndStudyPlan(List<Subjcet> subjects, StduyPlanEntity studyPlan);
}
