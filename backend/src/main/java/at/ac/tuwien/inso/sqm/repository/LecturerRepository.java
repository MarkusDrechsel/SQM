package at.ac.tuwien.inso.sqm.repository;

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface LecturerRepository extends CrudRepository<LecturerEntity, Long> {

    @Query("select l from LecturerEntity l where ACCOUNT_ID = ?1")
    LecturerEntity findLecturerByAccountId(Long id);


    public List<LecturerEntity> findAllByIdentificationNumberLikeIgnoreCaseOrNameLikeIgnoreCase(
            String identificationNumber,
            String name
    );

    public LecturerEntity findById(Long id);
}
