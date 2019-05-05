package at.ac.tuwien.inso.sqm.repository;

import at.ac.tuwien.inso.sqm.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CourseRepository extends CrudRepository<Lehrveranstaltung, Long> {

    Page<Lehrveranstaltung> findAllBySemesterAndSubjectNameLikeIgnoreCase(Semester semester, String name, Pageable pageable);

    List<Lehrveranstaltung> findAllBySemesterAndSubject(Semester semester, Subjcet subject);
    
    List<Lehrveranstaltung> findAllBySubject(Subjcet subject);

    @Query("select c " +
            "from Lehrveranstaltung c " +
            "where c.semester = (" +
            "   select s " +
            "   from Semester s " +
            "   where s.id = ( " +
            "       select max(s1.id) " +
            "       from Semester s1 " +
            "       )" +
            "   ) " +
            "and :student not member of c.students " +
            "and c.subject not in (" +
            "   select g.course.subject " +
            "   from Grade g " +
            "   where g.student = :student and g.mark.mark <> 5" +
            ") " +
            "and c not in :#{#student.dismissedCourses}")
    List<Lehrveranstaltung> findAllRecommendableForStudent(@Param("student") StudentEntity student);

    @Query("select c " +
            "from Lehrveranstaltung c " +
            "where ?1 member of c.students")
    List<Lehrveranstaltung> findAllForStudent(StudentEntity student);

    @Query("select case when count(c) > 0 then true else false end " +
            "from Lehrveranstaltung c " +
            "where c = ?2 and ?1 member of c.students"
    )
    boolean existsCourseRegistration(StudentEntity student, Lehrveranstaltung course);

	List<Lehrveranstaltung> findAllBySemester(Semester entity);
}