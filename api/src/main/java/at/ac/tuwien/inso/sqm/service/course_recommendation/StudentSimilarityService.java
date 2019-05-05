package at.ac.tuwien.inso.sqm.service.course_recommendation; //FIXME package naming convention?!

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;

//TODO javadoc
public interface StudentSimilarityService {

    //TODO how is similarity defined? - add javadoc
    List<StudentEntity> getSimilarStudents(StudentEntity student);
}
