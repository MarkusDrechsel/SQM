package at.ac.tuwien.inso.sqm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import at.ac.tuwien.inso.sqm.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.ac.tuwien.inso.sqm.entity.Feedback;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.FeedbackRepository;
import at.ac.tuwien.inso.sqm.repository.GradeRepository;
import at.ac.tuwien.inso.sqm.repository.TagRepository;
import at.ac.tuwien.inso.sqm.service.course_recommendation.TagFrequencyCalculator;

@Service
public class TagFrequencyCalculatorImpl implements TagFrequencyCalculator {

    public static final Map<MarkEntity, Double> gradeWeights = new HashMap<MarkEntity, Double>() {
        {
            put(MarkEntity.EXCELLENT, 0.5);
            put(MarkEntity.GOOD, 0.3);
            put(MarkEntity.SATISFACTORY, 0.1);
            put(MarkEntity.SUFFICIENT, 0.1);
            put(MarkEntity.FAILED, -0.5);
        }
    };

    public static final Map<Feedback.Type, Double> feedbackWeights = new HashMap<Feedback.Type, Double>() {
        {
            put(Feedback.Type.LIKE, 1.0);
            put(Feedback.Type.DISLIKE, -1.0);
        }
    };

    @Autowired
    TagRepository tagRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    @Override
    public Map<Tag, Double> calculate(StudentEntity student) {
        List<Lehrveranstaltung> courses = courseRepository.findAllForStudent(student);
        List<Grade> grades = gradeRepository.findAllOfStudent(student);
        List<Feedback> feedbacks = feedbackRepository.findAllOfStudent(student);

        Map<Tag, Double> tagFrequencies = calculateTagFrequency(courses);
        Map<Tag, Double> tagFrequenciesWithGrades = calculateTagFrequencyWithGrades(courses, grades);
        Map<Tag, Double> tagFrequenciesWithFeedback = calculateTagFrequencyWithFeedback(courses, feedbacks);

        return mergeTagFrequency(mergeTagFrequency(tagFrequencies, tagFrequenciesWithGrades), tagFrequenciesWithFeedback);
    }

    private Map<Tag, Double> calculateTagFrequency(List<Lehrveranstaltung> courses) {
        Map<Tag, Double> tags = new HashMap<>();
        courses.forEach(course ->
                course.getTags().forEach(it -> tags.put(it, tags.getOrDefault(it, 0.0) + 1))
        );

        return tags;
    }

    private Map<Tag, Double> calculateTagFrequencyWithGrades(List<Lehrveranstaltung> courses, List<Grade> grades) {
        Map<Tag, Double> tagsWithGrades = new HashMap<>();
        courses.forEach(course -> {
            double score = grades.stream()
                    .filter(grade -> grade.getCourse().equals(course))
                    .map(grade -> gradeWeights.getOrDefault(grade.getMark(), 0.0))
                    .findAny()
                    .orElse(0.0);

            course.getTags().forEach(tag -> tagsWithGrades.put(tag, tagsWithGrades.getOrDefault(tag, 0.0) + score));
        });

        return tagsWithGrades;
    }

    private Map<Tag, Double> calculateTagFrequencyWithFeedback(List<Lehrveranstaltung> courses, List<Feedback> feedbacks) {
        Map<Tag, Double> tagsWithFeedback = new HashMap<>();

        courses.forEach(course -> {
            double score = feedbacks.stream()
                    .filter(feedback -> feedback.getCourse().equals(course))
                    .map(feedback -> feedbackWeights.getOrDefault(feedback.getType(), 0.0))
                    .findAny()
                    .orElse(0.0);

            course.getTags().forEach(tag -> tagsWithFeedback.put(tag, tagsWithFeedback.getOrDefault(tag, 0.0) + score));
        });

        return tagsWithFeedback;
    }

    private Map<Tag, Double> mergeTagFrequency(Map<Tag, Double> map1, Map<Tag, Double> map2) {
        return Stream
                .concat(map1.entrySet().stream(), map2.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Double::sum
                        )
                );
    }
}
