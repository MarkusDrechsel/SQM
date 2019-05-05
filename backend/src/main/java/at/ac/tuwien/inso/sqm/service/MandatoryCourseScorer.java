package at.ac.tuwien.inso.sqm.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.entity.Subjcet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.repository.SubjectForStudyPlanRepository;
import at.ac.tuwien.inso.sqm.service.course_recommendation.CourseScorer;

@Service
public class MandatoryCourseScorer implements CourseScorer {

    private static final double MANDATORY = 3;

    @Autowired
    private SubjectForStudyPlanRepository subjectForStudyPlanRepository;

    @Value("${uis.course.recommender.mandatory.scorer.weight:1}")
    private Double weight;

    @Override
    public double weight() {
        return weight;
    }

    @Override
    public Map<Lehrveranstaltung, Double> score(List<Lehrveranstaltung> courses, StudentEntity student) {
        Map<Lehrveranstaltung, Double> scoredCourses = courses.stream().collect(toMap(identity(), it -> 0.0));
        List<Subjcet> subjectsForCourses = courses.stream().map(course -> course.getSubject()).collect(toList());

        student.getStudyplans().forEach(studyPlanRegistration ->
                subjectForStudyPlanRepository.findBySubjectInAndStudyPlan(
                        subjectsForCourses,
                        studyPlanRegistration.getStudyplan()
                ).stream()
                        .filter(subjectForStudyPlan -> subjectForStudyPlan.getMandatory())
                        .forEach(subjectForStudyPlan -> {
                            Lehrveranstaltung course = courses.stream().filter(it -> it.getSubject().equals(subjectForStudyPlan.getSubject())).findFirst().get();
                            scoredCourses.put(course, scoredCourses.get(course) + MANDATORY);
                        })

        );

        return scoredCourses;
    }
}
