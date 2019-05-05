package at.ac.tuwien.inso.sqm.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import at.ac.tuwien.inso.sqm.entity.*;
import at.ac.tuwien.inso.sqm.service.course_recommendation.CourseRelevanceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectForStudyPlanRepository;

@Component
public class SemesterRecommendationCourseRelevanceFilter implements CourseRelevanceFilter {

    @Autowired
    private SemestreRepository semesterRepository;

    @Autowired
    private SubjectForStudyPlanRepository subjectForStudyPlanRepository;

    @Override
    public List<Lehrveranstaltung> filter(List<Lehrveranstaltung> courses, StudentEntity student) {
        Map<StduyPlanEntity, Integer> studentSemesters = student.getStudyplans().stream()
                .collect(toMap(
                        StudyPlanRegistration::getStudyplan,
                        it -> semesterRepository.findAllSince(it.getRegisteredSince()).size())
                );

        List<Subjcet> subjects = courses.stream().map(Lehrveranstaltung::getSubject).collect(Collectors.toList());
        Map<StduyPlanEntity, Map<Subjcet, Integer>> courseSemesterRecommendations = student.getStudyplans().stream()
                .map(StudyPlanRegistration::getStudyplan)
                .collect(toMap(
                        identity(),
                        it -> {
                            Map<Subjcet, Integer> subjectToSemesterRecommendation = new HashMap<>();
                            subjectForStudyPlanRepository.findBySubjectInAndStudyPlan(subjects, it).forEach(subjectForStudyPlan -> {
                                subjectToSemesterRecommendation.put(subjectForStudyPlan.getSubject(), subjectForStudyPlan.getSemesterRecommendation());
                            });
                            return subjectToSemesterRecommendation;
                        }
                ));

        return courses.stream().filter(course ->
                studentSemesters.keySet().stream().anyMatch(studyPlan -> {
                    Integer studentSemester = studentSemesters.get(studyPlan);
                    Integer courseSemester = courseSemesterRecommendations.get(studyPlan).get(course.getSubject());

                    return courseSemester == null ||
                            studentSemester >= courseSemester;
                })
        ).collect(Collectors.toList());
    }
}
