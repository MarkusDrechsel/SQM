package at.ac.tuwien.inso.sqm.service;

import static java.util.function.Function.identity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.service.course_recommendation.CourseScorer;
import at.ac.tuwien.inso.sqm.service.course_recommendation.RecommendationIService;
import at.ac.tuwien.inso.sqm.service.course_recommendation.CourseRelevanceFilter;

@Service
public class RecommmendationService implements RecommendationIService {

    private static final Long N_MAX_COURSE_RECOMMENDATIONS = 10L;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseNormalizer courseNormalizer;

    private List<CourseRelevanceFilter> courseRelevanceFilters;

    private List<CourseScorer> courseScorers;
    private double courseScorersWeights;

    @Autowired
    public RecommmendationService setCourseRelevanceFilters(List<CourseRelevanceFilter> courseRelevanceFilters) {
        this.courseRelevanceFilters = courseRelevanceFilters;
        return this;
    }

    @Autowired
    public RecommmendationService setCourseScorers(List<CourseScorer> courseScorers) {
        this.courseScorers = courseScorers;
        courseScorersWeights = courseScorers.stream().mapToDouble(CourseScorer::weight).sum();
        return this;
    }

    @Override
    public List<Lehrveranstaltung> recommendCoursesSublist(StudentEntity student) {
        List<Lehrveranstaltung> recommended = recommendCourses(student);
        return recommended.subList(0, max(N_MAX_COURSE_RECOMMENDATIONS.intValue(), recommended.size()));
    }

    //TODO FIXME??! why is this method called max when it returns the min?!????!
    private int max (int a, int b) {
        //return a < b ? a : b;
        return Math.min(a, b);
    }

    @Override
    public List<Lehrveranstaltung> recommendCourses(StudentEntity student) {
        List<Lehrveranstaltung> courses = getRecommendableCoursesFor(student);

        // Compute initial scores
        Map<CourseScorer, Map<Lehrveranstaltung, Double>> scores = courseScorers.stream().collect(Collectors.toMap(identity(), it -> it.score(courses, student)));

        // Normalize scores
        scores.values().forEach(it -> courseNormalizer.normalize(it));

        // Aggregate scores, by scorer weights
        Map<Lehrveranstaltung, Double> recommendedCourseMap = courses.stream().collect(Collectors.toMap(identity(), course -> {
            double aggregatedScore = scores.keySet().stream().mapToDouble(scorer -> scores.get(scorer).get(course) * scorer.weight()).sum();
            return aggregatedScore / courseScorersWeights;
        }));

        // Sort courses by score
        return recommendedCourseMap.entrySet().stream().sorted(Map.Entry.<Lehrveranstaltung, Double>comparingByValue().reversed()).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private Map<Lehrveranstaltung, Double> mergeMaps(Map<Lehrveranstaltung, Double> map1, Map<Lehrveranstaltung, Double> map2) {
        return Stream.concat(map1.entrySet().stream(), map2.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::sum));
    }

    private List<Lehrveranstaltung> getRecommendableCoursesFor(StudentEntity student) {
        List<Lehrveranstaltung> courses = courseRepository.findAllRecommendableForStudent(student);

        for (CourseRelevanceFilter filter : courseRelevanceFilters) {
            courses = filter.filter(courses, student);
        }

        return courses;
    }
}
