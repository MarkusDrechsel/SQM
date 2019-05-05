package at.ac.tuwien.inso.sqm.service;

import java.util.Map;
import java.util.stream.Collectors;

import at.ac.tuwien.inso.sqm.service.course_recommendation.Normalizer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.util.hash.Hash;
import org.springframework.stereotype.Component;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;

@Component
public class CourseNormalizer implements Normalizer {

    @Override
    public void normalize(Map<Lehrveranstaltung, Double> courses) {
        double max = courses.values().stream().collect(Collectors.summarizingDouble(Double::doubleValue)).getMax();
        double min = courses.values().stream().collect(Collectors.summarizingDouble(Double::doubleValue)).getMin();

        if (max - min == 0)
            return;

        courses.keySet().forEach(course -> {
                    double value = (courses.get(course) - min) / (max - min);
                    courses.put(course, value);
                }
        );
    }
}
