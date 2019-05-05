package at.ac.tuwien.inso.sqm.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import at.ac.tuwien.inso.sqm.entity.Tag;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    @Deprecated
    public List<Tag> findAll();

    public List<Tag> findByValidTrue();

    Tag findByName(String name);
}
