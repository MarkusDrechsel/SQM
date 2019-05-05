package at.ac.tuwien.inso.sqm.repository;

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import at.ac.tuwien.inso.sqm.repository.utils.TagFrequency;

public interface StduentRepository extends CrudRepository<StudentEntity, Long> {

    @Query("select new at.ac.tuwien.inso.sqm.repository.utils.TagFrequency(t, count(t)) " +
            "from Lehrveranstaltung c join c.tags t " +
            "where ?1 member of c.students " +
            "group by t")
    List<TagFrequency> computeTagsFrequencyFor(StudentEntity student);

    StudentEntity findByAccount(UserAccountEntity account);

    @Query("select s " +
            "from StudentEntity s join s.account a " +
            "where a.username = ?1")
    StudentEntity findByUsername(String username);
}
