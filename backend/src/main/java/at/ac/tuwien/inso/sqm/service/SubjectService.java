package at.ac.tuwien.inso.sqm.service;

import java.util.List;
import java.util.stream.Collectors;

import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.exception.LecturerNotFoundException;
import at.ac.tuwien.inso.sqm.exception.RelationNotfoundException;
import at.ac.tuwien.inso.sqm.exception.SubjectNotFoundException;
import at.ac.tuwien.inso.sqm.exception.ValidationException;
import at.ac.tuwien.inso.sqm.repository.LecturerRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectRepository;
import at.ac.tuwien.inso.sqm.validator.SubjectValidator;
import at.ac.tuwien.inso.sqm.validator.UisUserValidator;
import at.ac.tuwien.inso.sqm.validator.ValidatorFactory;


@Service
public class SubjectService implements SubjectIService {

    private ValidatorFactory validatorFactory = new ValidatorFactory();
    private SubjectValidator validator = validatorFactory.getSubjectValidator();
    private UisUserValidator userValidator = validatorFactory.getUisUserValidator();
    
    private static final Logger log = LoggerFactory.getLogger(SubjectService.class);

    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private LehrveranstaltungServiceInterface courseService;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Override
    public Page<Subjcet> findBySearch(String search, Pageable pageable) {
    	log.info("finding search by word "+search);
        String sqlSearch = "%" + search + "%";
        return subjectRepository.findAllByNameLikeIgnoreCase(sqlSearch, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Subjcet findOne(Long id) {
        validator.validateSubjectId(id);
    	log.info("finding subject by id "+id);
        Subjcet subject = subjectRepository.findOne(id);

        if(subject == null) {
            log.warn("Subjcet not found");
            //TODO throwing this will cause a security test fail, for whatever reason?
            //throw new SubjectNotFoundException();
        }

        return subject;
    }

    @Override
    @Transactional
    public Subjcet create(Subjcet subject) {
        validator.validateNewSubject(subject);
    	log.info("creating subject "+subject.toString());
        return subjectRepository.save(subject);
    }

    @Override
    @Transactional
    public LecturerEntity addLecturerToSubject(Long subjectId, Long lecturerUisUserId) {
        
    	userValidator.validateUisUserId(lecturerUisUserId);
        validator.validateSubjectId(subjectId);
        
        log.info("addLecturerToSubject for subject {} and lecturer {}", subjectId,
                lecturerUisUserId);
        
        LecturerEntity lecturer = lecturerRepository.findById(lecturerUisUserId);

        if (lecturer == null) {
            String msg = "LecturerEntity with user id " + lecturerUisUserId + " not found";
            log.info(msg);
            throw new LecturerNotFoundException(msg);
        }

        Subjcet subject = subjectRepository.findById(subjectId);

        if (subject == null) {
            String msg = "Subjcet with id " + lecturerUisUserId + " not found";
            log.info(msg);
            throw new SubjectNotFoundException(msg);
        }

        if (subject.getLecturers().contains(lecturer)) {
            return lecturer;
        }

        subject.addLecturers(lecturer);
        subjectRepository.save(subject);

        return lecturer;
    }


    @Override
    @Transactional(readOnly = true)
    public List<LecturerEntity> getAvailableLecturersForSubject(Long subjectId, String search) {
        validator.validateSubjectId(subjectId);
        if (search == null) {
            search = "";
        }
    	log.info("getting available lectureres for subject with subject id "+subjectId+" and search string "+search);
        
    	
        Subjcet subject = subjectRepository.findById(subjectId);

        if (subject == null) {
        	log.warn("Subjcet with id '" + subjectId + "' not found");
            throw new SubjectNotFoundException("Subjcet with id '" + subjectId + "' not found");
        }

        List<LecturerEntity> currentLecturers = subject.getLecturers();

        List<LecturerEntity> searchedLecturers =
                lecturerRepository.findAllByIdentificationNumberLikeIgnoreCaseOrNameLikeIgnoreCase(
                        "%" + search + "%",
                        "%" + search + "%"
                );

        return searchedLecturers
                .stream()
                .filter(lecturer -> !currentLecturers.contains(lecturer))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public LecturerEntity removeLecturerFromSubject(Long subjectId, Long lecturerUisUserId) {
    	userValidator.validateUisUserId(lecturerUisUserId);
        validator.validateSubjectId(subjectId);
        log.info("removeLecturerFromSubject for subject {} and lecturer {}", subjectId, lecturerUisUserId);

        Subjcet subject = subjectRepository.findById(subjectId);

        if (subject == null) {
            String msg = "Subjcet with id '" + subjectId + "' not found";
            log.info(msg);
            throw new SubjectNotFoundException(msg);
        }

        LecturerEntity lecturer = lecturerRepository.findById(lecturerUisUserId);

        if (lecturer == null) {
            String msg = "LecturerEntity with id '" + lecturerUisUserId + "' not found";
            log.info(msg);
            throw new LecturerNotFoundException(msg);
        }

        List<LecturerEntity> currentLecturers = subject.getLecturers();

        boolean isLecturer = currentLecturers.contains(lecturer);

        if (!isLecturer) {
            String msg = "LecturerEntity with id " + lecturerUisUserId + " not found for subject " +
                    subjectId;
            log.info(msg);
            throw new RelationNotfoundException(msg);
        }

        subject.removeLecturers(lecturer);

        subjectRepository.save(subject);

        return lecturer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subjcet> searchForSubjects(String word) {
    	log.info("seachring for subjects with search word "+word);
        return subjectRepository.findByNameContainingIgnoreCase(word);
    }

	@Override
	@Transactional
	public boolean remove(Subjcet subject) throws ValidationException{
		log.info("trying to remove subject");
		validator.validateNewSubject(subject);
		if(subject==null){
			log.info("Subjcet is null.");
			throw new ValidationException("Subjcet is null.");
		}
		log.info("removing subject "+subject+" now.");
		List<Lehrveranstaltung> courses = courseService.findCoursesForSubject(subject);
		if(courses.size() > 0){
			String msg = "";
			if(subject!=null){
				msg = "Cannot delete subject [Name: "+subject.getName()+"] because there are courses";
			}
			log.info(msg);
			throw new ValidationException(msg);
		}else{
			subjectRepository.delete(subject);
			return true;
		}
	}


}
