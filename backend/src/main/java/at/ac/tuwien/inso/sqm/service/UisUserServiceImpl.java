package at.ac.tuwien.inso.sqm.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.UisUserEntity;
import at.ac.tuwien.inso.sqm.exception.BusinessObjectNotFoundException;
import at.ac.tuwien.inso.sqm.repository.UisUserRepository;
import at.ac.tuwien.inso.sqm.validator.UisUserValidator;
import at.ac.tuwien.inso.sqm.validator.ValidatorFactory;

@Service
public class UisUserServiceImpl implements UisUserServiceInterface {

    private ValidatorFactory validatorFactory = new ValidatorFactory();
    private UisUserValidator validator = validatorFactory.getUisUserValidator();

	private static final Logger log = LoggerFactory.getLogger(UisUserServiceImpl.class);
	
    @Autowired
    private UisUserRepository uisUserRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UisUserEntity> findAllMatching(String searchFilter, Pageable pageable) {
    	log.info("find all matching UisUsers for searchFilter "+searchFilter+" and Pageable "+pageable);
        return uisUserRepository.findAllMatching(searchFilter, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public UisUserEntity findOne(long id) throws BusinessObjectNotFoundException {
    	log.info("finding UisUserEntity for id "+id);
        validator.validateUisUserId(id);
        UisUserEntity user = uisUserRepository.findOne(id);
        if (user == null) {
        	log.warn("can not find UisUserEntity: Invalid user id: " + id );
        	throw new BusinessObjectNotFoundException("Invalid user id: " + id);
        }

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUserWithIdentificationNumber(String id) {
    	log.info("trying to find boolean if user with id "+id+" exists.");
        return uisUserRepository.existsByIdentificationNumber(id);
    }

	@Override
	@Transactional(readOnly = true)
	public boolean existsUserWithMailAddress(String email) {
		log.info("trying to find a user by email adress");
		List<UisUserEntity> l = uisUserRepository.findAllByEmail(email);
		
		if(l!=null&&l.size() > 0){
			log.info("found users with same email adress");
			return true;
		}else{
			log.info("found no users with same email adress");
			return false;
		}
	}
}
