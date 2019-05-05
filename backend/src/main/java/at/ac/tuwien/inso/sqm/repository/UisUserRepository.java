package at.ac.tuwien.inso.sqm.repository;

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.UisUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UisUserRepository extends JpaRepository<UisUserEntity, Long> {

    @Query("select u " +
            "from UisUserEntity u " +
            "where lower(u.identificationNumber) like concat('%', lower(?1), '%') " +
            "or lower(u.name) like concat('%', lower(?1), '%') " +
            "or lower(u.email) like concat('%', lower(?1), '%') " +
            "order by u.id desc"
    )
    Page<UisUserEntity> findAllMatching(String searchFilter, Pageable pageable);

    @Query("select case when count(user) > 0 then true else false end " +
            "from UisUserEntity user " +
            "where user.identificationNumber = ?1"
    )
    boolean existsByIdentificationNumber(String identificationNumber);

	List<UisUserEntity> findAllByEmail(String email);
}
