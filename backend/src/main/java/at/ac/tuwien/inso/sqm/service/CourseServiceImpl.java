package at.ac.tuwien.inso.sqm.service;

import at.ac.tuwien.inso.sqm.dto.*;
import at.ac.tuwien.inso.sqm.entity.*;
import at.ac.tuwien.inso.sqm.exception.*;
import at.ac.tuwien.inso.sqm.repository.*;
import at.ac.tuwien.inso.sqm.service.student_subject_prefs.*;
import at.ac.tuwien.inso.sqm.validator.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.context.i18n.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import javax.validation.constraints.*;
import java.util.*;

@Service
public class CourseServiceImpl implements LehrveranstaltungServiceInterface {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);
    private ValidatorFactory validatorFactory = new ValidatorFactory();
    private CourseValidator validator = validatorFactory.getCourseValidator();

    @Autowired
    private SemesterServiceInterface semesterService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StduentRepository stduentRepository;

    @Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private GradeIService gradeService;

    @Autowired
    private TgaService tgaService;

    @Autowired
    private SubjectForStudyPlanRepository subjectForStudyPlanRepository;

    @Autowired
    private StudentSubjectPreferenceStore studentSubjectPreferenceStore;

    @Override
    @Transactional(readOnly = true)
    public Page<Lehrveranstaltung> findCourseForCurrentSemesterWithName(@NotNull String name, Pageable pageable) {
        log.info("try to find course for current semester with semestername: " + name + "and pageable " + pageable);
        Semester semester = semesterService.getOrCreateCurrentSemester().toEntity();
        return courseRepository.findAllBySemesterAndSubjectNameLikeIgnoreCase(semester, "%" + name + "%", pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lehrveranstaltung> findCoursesForCurrentSemesterForLecturer(LecturerEntity lecturer) {
        log.info("try finding courses for current semester for lecturer with id " + lecturer.getId());
        Semester semester = semesterService.getOrCreateCurrentSemester().toEntity();
        Iterable<Subjcet> subjectsForLecturer = subjectRepository.findByLecturers_Id(lecturer.getId());
        List<Lehrveranstaltung> courses = new ArrayList<>();
        subjectsForLecturer.forEach(subject -> courses.addAll(courseRepository.findAllBySemesterAndSubject(semester, subject)));
        return courses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lehrveranstaltung> findCoursesForSubject(Subjcet subject) {
        log.info("try finding course for subject with id " + subject.getId());
        return courseRepository.findAllBySubject(subject);
    }

    @Override
    public List<Lehrveranstaltung> findCoursesForSubjectAndCurrentSemester(Subjcet subject) {
        List<Lehrveranstaltung> result = courseRepository.findAllBySemesterAndSubject(semesterService.getCurrentSemester().toEntity(), subject);
        return result;
    }

    @Override
    @Transactional
    public void dismissCourse(StudentEntity student, Long courseId) {
        Lehrveranstaltung course = findeLehrveranstaltung(courseId);
        student.addDismissedCourse(course);
    }

    @Override
    @Transactional
    public Lehrveranstaltung saveCourse(AddCourseForm form) {
        log.info("try saving course");
        Lehrveranstaltung course = form.getCourse();
        validator.validateNewCourse(course);
        
        UserAccountEntity u = userAccountService.getCurrentLoggedInUser();
        
        isLecturerAllowedToChangeCourse(course, u);
        
        log.info("try saving course " + course.toString());

        ArrayList<Tag> currentTagsOfCourse = new ArrayList<>(form.getCourse().getTags());

        for (String tag : form.getTags()) {
            Tag newTag = tgaService.findByName(tag);

            // tag doesn't exist, so create a new one.
            if (newTag == null) {
                course.addTags(new Tag(tag));
            }
            // tag exists, but not in this course
            else if (!course.getTags().contains(newTag)) {
                course.addTags(newTag);
            }
            // tag already exists for this course
            else {
                currentTagsOfCourse.remove(newTag);
            }
        }

        // the remaining tags are to be removed
        course.removeTags(currentTagsOfCourse);

        if (!(course.getStudentLimits() > 0)) {
            course.setStudentLimits(1);
        }
        return courseRepository.save(course);
    }
    
    private void isLecturerAllowedToChangeCourse(Lehrveranstaltung c, UserAccountEntity u){
    	if(c==null||u==null){
    		String msg = messageSource.getMessage("lecturer.course.edit.error.notallowed", null, LocaleContextHolder.getLocale());
    		throw new ValidationException(msg);
    	}
    	
    	if(u.hasRole(Rolle.ADMIN)){
    		log.info("user is admin, therefore course modification is allowed");;
    		return;
    	}
    	
    	for(LecturerEntity l : c.getSubject().getLecturers()){
    		if(l.getAccount().equals(u)){
    			log.info("found equal lecturers, course modification is allowed");;
    			return;
    		}
    	}
    	log.warn("suspisious try to modify course. user is not admin or does not own the subject for this course");
    	String msg = messageSource.getMessage("lecturer.course.edit.error.notallowed", null, LocaleContextHolder.getLocale());
    	throw new ValidationException(msg);
    }

    @Override
    @Transactional(readOnly = true)
    public Lehrveranstaltung findeLehrveranstaltung(Long id) {
        log.info("try finding course with id " + id);
        Lehrveranstaltung course = courseRepository.findOne(id);
        if (course == null) {
            log.warn("Lehrveranstaltung with id " + id + " does not exist");
            throw new BusinessObjectNotFoundException("Lehrveranstaltung with id " + id + " does not exist");
        }
        return course;
    }

    @Override
    @Transactional
    public boolean remove(Long courseId) throws ValidationException {
        log.info("try removing  course with id " + courseId);
        validator.validateCourseId(courseId); // throws ValidationException
        Lehrveranstaltung course = courseRepository.findOne(courseId);

        if (course == null) {
            String msg = "Lehrveranstaltung can not be deleted because there is no couse found with id " + courseId;
            log.warn(msg);
            throw new BusinessObjectNotFoundException(msg);
        }
        
        isLecturerAllowedToChangeCourse(course, userAccountService.getCurrentLoggedInUser());


        List<Grade> grades = gradeService.findAllByCourseId(courseId);

        if (grades != null && grades.size() > 0) {
            String msg = "There are grades for course [id:" + courseId + "], therefore integrationtest can not be removed.";
            log.warn(msg);
            throw new ValidationException(msg);
        }

        if (course.getStudents().size() > 0) {
            String msg = "There are students for course [id:" + courseId + "], therefore integrationtest can not be removed.";
            log.warn(msg);
            throw new ValidationException(msg);
        }

        log.info("successfully validated course removal. removing now!");
        courseRepository.delete(course);
        return true;
    }


    @Override
    @Transactional
    public boolean studentZurLehrveranstaltungAnmelden(Lehrveranstaltung lehrveranstaltung) {
        validator.validateCourse(lehrveranstaltung);
        validator.validateCourseId(lehrveranstaltung.getId());
        StudentEntity student = stduentRepository.findByUsername(userAccountService.getCurrentLoggedInUser().getUsername());

        log.info("try registering currently logged in student with id " + student.getId() + " for course with id " + lehrveranstaltung.getId());
        if (lehrveranstaltung.getStudentLimits() <= lehrveranstaltung.getStudents().size()) {
            return false;
        } else if (lehrveranstaltung.getStudents().contains(student)) {
            return true;
        } else {
            lehrveranstaltung.addStudents(student);
            courseRepository.save(lehrveranstaltung);
            studentSubjectPreferenceStore.studentRegisteredCourse(student, lehrveranstaltung);
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lehrveranstaltung> findAllForStudent(StudentEntity student) {
        log.info("finding all courses for student with id " + student.getId());
        return courseRepository.findAllForStudent(student);
    }

    @Override
    @Transactional
    public Lehrveranstaltung studentVonLehrveranstaltungAbmelden(StudentEntity student, Long lehrveranstaltungsID) {
        log.info("Unregistering student with id {} from course with id {}", student.getId(), lehrveranstaltungsID);
        validator.validateCourseId(lehrveranstaltungsID);
        
        Lehrveranstaltung course = courseRepository.findOne(lehrveranstaltungsID);
        if (course == null) {
            log.warn("Lehrveranstaltung with id {} not found. Nothing to unregister", lehrveranstaltungsID);
            throw new BusinessObjectNotFoundException();
        }

        UserAccountEntity currentLoggedInUser = userAccountService.getCurrentLoggedInUser();
        //students should only be able to unregister themselves
        if(currentLoggedInUser.hasRole(Rolle.STUDENT)){
        	if(!student.getAccount().getUsername().equals(userAccountService.getCurrentLoggedInUser().getUsername())){
        		log.warn("student with id {} and username {} tried to unregister another one with id {} and username {}", userAccountService.getCurrentLoggedInUser().getId(), userAccountService.getCurrentLoggedInUser().getUsername(), student.getId(), student.getAccount().getUsername());
        		String msg = messageSource.getMessage("lecturer.course.edit.error.notallowed", null, LocaleContextHolder.getLocale());
        		throw new ValidationException(msg);
        	}
        }
        
        //Lectureres should only be able to remove students from their own courses
        if(currentLoggedInUser != null  && currentLoggedInUser.hasRole(Rolle.LECTURER)){
        	isLecturerAllowedToChangeCourse(course, userAccountService.getCurrentLoggedInUser());
        }
        

        course.removeStudents(student);
        studentSubjectPreferenceStore.studentUnregisteredCourse(student, course);
        return course;
    }

    private boolean checkRole(Rolle role) {
        return userAccountService.getCurrentLoggedInUser().hasRole(role);
    }

    @Override
    public CoruseDetailsForStudent courseDetailsFor(StudentEntity student, Long courseId) {
        validator.validateCourseId(courseId);
        validator.validateStudent(student);
        log.info("reading course details for student with id " + student.getId() + " from course with id " + courseId);
        Lehrveranstaltung course = findeLehrveranstaltung(courseId);
        if (course == null) {
            log.warn("Lehrveranstaltung with id {} not found. Nothing to unregister", courseId);
            throw new BusinessObjectNotFoundException();
        }


        return new CoruseDetailsForStudent(course).setCanEnroll(canEnrollToCourse(student, course)).setStudyplans(subjectForStudyPlanRepository.findBySubject(course.getSubject()));
    }

    @Override
    public List<SubjectForStudyPlanEntity> getSubjectForStudyPlanList(Lehrveranstaltung course) {
        validator.validateCourse(course);
        return subjectForStudyPlanRepository.findBySubject(course.getSubject());
    }

    private boolean canEnrollToCourse(StudentEntity student, Lehrveranstaltung course) {
        validator.validateCourse(course);
        validator.validateStudent(student);
        return course.getSemester().toDto().equals(semesterService.getOrCreateCurrentSemester()) && !courseRepository.existsCourseRegistration(student, course);
    }

	@Override
	public List<Lehrveranstaltung> findAllCoursesForCurrentSemester() {
		SemesterDto semester = semesterService.getOrCreateCurrentSemester();
		return courseRepository.findAllBySemester(semester.toEntity());	
	}
}
