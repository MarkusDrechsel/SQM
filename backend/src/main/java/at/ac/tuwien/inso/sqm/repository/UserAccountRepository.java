package at.ac.tuwien.inso.sqm.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import at.ac.tuwien.inso.sqm.entity.UserAccountEntity;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccountEntity, Long> {

    UserAccountEntity findByUsername(String username);

    @Query("select case when count(u) > 0 then true else false end " +
            "from UserAccountEntity u " +
            "where u.username = ?1")
    boolean existsByUsername(String username);
}
