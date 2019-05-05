package at.ac.tuwien.inso.sqm.repository;

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.Subjcet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends CrudRepository<Subjcet, Long> {

    List<Subjcet> findByLecturers_Id(Long id);

    Subjcet findById(Long id);

    List<Subjcet> findByNameContainingIgnoreCase(String name);

    Page<Subjcet> findAll(Pageable pageable);

    Page<Subjcet> findAllByNameLikeIgnoreCase(String name, Pageable pageable);
}
