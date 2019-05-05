package at.ac.tuwien.inso.sqm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.entity.Tag;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.service.course_recommendation.CourseScorer;

@Service
public class TagFrequencyScoorer implements CourseScorer {

    //private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GradeService.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TagFrequencyCalculatorImpl tagFrequencyCalculator;

    @Value("${uis.course.recommender.tag.scorer.weight:1}")
    private Double weight;

    @Override
    public double weight() {
        return weight;
    }

    @Override
    public Map<Lehrveranstaltung, Double> score(List<Lehrveranstaltung> courses, StudentEntity student) {
        Map<Tag, Double> tagFrequencies = tagFrequencyCalculator.calculate(student);
        Map<Lehrveranstaltung, Double> scoredCourses = new HashMap<>();

        courses.forEach(course -> {
                    double score = course.getTags().stream()
                            .filter(tag -> tagFrequencies.containsKey(tag))
                            .mapToDouble(tag -> tagFrequencies.get(tag)).sum();

            //log.info("Score for course {} is: {}", course, score);
            scoredCourses.put(course, score);
                }
        );

        return scoredCourses;
    }
}
