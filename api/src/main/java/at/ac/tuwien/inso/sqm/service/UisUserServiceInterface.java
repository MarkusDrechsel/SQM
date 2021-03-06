package at.ac.tuwien.inso.sqm.service;

import at.ac.tuwien.inso.sqm.entity.UisUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import at.ac.tuwien.inso.sqm.exception.BusinessObjectNotFoundException;

public interface UisUserServiceInterface {

	/**
	 * returns all matching UisUsers that fit the searchFilter and are on the provided Pageable.
	 * the query should be ordered desc. by the user IDs
	 * can only be used by ADMINs.
	 *
	 * 
	 * @param searchFilter
	 * @param pageable
	 * @return
	 */
    @PreAuthorize("hasRole('ADMIN')")
    Page<UisUserEntity> findAllMatching(String searchFilter, Pageable pageable);

    /**
     * 
     * @param id should not be null and not <1
     * @return UisUserEntity
     * @throws BusinessObjectNotFoundException
     * 
     * returns an {@link UisUserEntity} with the provided id. if no user can be found
     * can only be used by ADMINs. if no user is found a {@link BusinessObjectNotFoundException} will be thrown
     */
    @PreAuthorize("hasRole('ADMIN')")
    UisUserEntity findOne(long id) throws BusinessObjectNotFoundException;

    /**
     * looks up if a UisUserEntity with the provided id exists in the repository. returns a true boolean if the id exists.
     * @param id
     * @return boolean
     */
    @PreAuthorize("isAuthenticated()")
    boolean existsUserWithIdentificationNumber(String id);

    /**
     * returns true if there already is a user with this emailadress
     * user has to be admin
     * 
     * @param email
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
	boolean existsUserWithMailAddress(String email);
}
