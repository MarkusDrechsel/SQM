package at.ac.tuwien.inso.sqm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import at.ac.tuwien.inso.sqm.service.course_recommendation.StudentNeighborhoodStore;
import at.ac.tuwien.inso.sqm.service.course_recommendation.StudentSimilarityService;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.repository.StduentRepository;

@Service
public class StduentSimilarityServiceImpl implements StudentSimilarityService {

    @Autowired
    private StudentNeighborhoodStore studentNeighborhoodStore;

    @Autowired
    private StduentRepository stduentRepository;

    @Override
    public List<StudentEntity> getSimilarStudents(StudentEntity student) {
        UserNeighborhood userNeighborhood = studentNeighborhoodStore.getStudentNeighborhood();
        long[] userIds;
        try {
            userIds = userNeighborhood.getUserNeighborhood(student.getId());
        } catch (TasteException e) {
            return Collections.emptyList();
        }

        ArrayList<StudentEntity> students = new ArrayList<>();
        Arrays.stream(userIds).forEach(id -> students.add(stduentRepository.findOne(id)));

        return students;
    }
}
