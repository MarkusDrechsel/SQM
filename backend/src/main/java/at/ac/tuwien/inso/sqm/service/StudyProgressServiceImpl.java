package at.ac.tuwien.inso.sqm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import at.ac.tuwien.inso.sqm.entity.*;
import at.ac.tuwien.inso.sqm.service.study_progress.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.dto.SemesterDto;
import at.ac.tuwien.inso.sqm.entity.Feedback;
import at.ac.tuwien.inso.sqm.service.study_progress.CuorseRegistration;

@Service
public class StudyProgressServiceImpl implements StudyProgressService {

    @Autowired
    private SemesterServiceInterface semesterService;

    @Autowired
    private LehrveranstaltungServiceInterface courseService;

    @Autowired
    private GradeIService gradeService;

    @Autowired
    private FeedbackIService feedbackService;
    
    /*@Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private MessageSource messageSource;*/

    @Override
    @Transactional(readOnly = true)
    public StudyProgress studyProgressFor(StudentEntity student) {
    	SemesterDto currentSemester = semesterService.getOrCreateCurrentSemester();
        
    	/*//only the logged in student should be able to see his study progress. no other should be able to do this. (guard)
    	if(!userAccountService.getCurrentLoggedInUser().getId().equals(student.getId())){
    		String msg = messageSource.getMessage("lecturer.course.edit.error.notallowed", null, LocaleContextHolder.getLocale());
    		throw new ValidationException(msg);
    	}*/
    	
        List<SemesterDto> semesters = studentSemesters(student);
        List<Lehrveranstaltung> courses = courseService.findAllForStudent(student);
        List<Grade> grades = gradeService.findAllOfStudent(student);
        List<Feedback> feedbacks = feedbackService.findAllOfStudent(student);
        
      
        List<SemesterProgress> semestersProgress = semesters.stream()
                .map(it -> new SemesterProgress(it, courseRegistrations(it, currentSemester, courses, grades, feedbacks)))
                .collect(Collectors.toList());
        
        return new StudyProgress(currentSemester, semestersProgress);
    }

    private List<SemesterDto> studentSemesters(StudentEntity student) {
    	SemesterDto firstSem = getFirstSemesterFor(student);
    	if(firstSem!=null){
    		return semesterService.findAllSince(getFirstSemesterFor(student));
    	}else{
    		return new ArrayList<SemesterDto>();
    	}
    }

    /**
     * Get the first semester the student registered for
     *
     * TODO: WTF does this code? Please clean up!
     *
     * @param student
     * @return
     */
    private SemesterDto getFirstSemesterFor(StudentEntity student) {
        List<StudyPlanRegistration> registrations = student.getStudyplans();

        // TODO refactor: this is not a valid way to treat Semesters
    	SemesterDto min = new SemesterDto(Integer.MAX_VALUE, SemestreTypeEnum.SummerSemester);
    	min.setId(Long.MAX_VALUE);
    	
    	for(StudyPlanRegistration spr: registrations){
    		if(min!=null&spr!=null&&spr.getRegisteredSince()!=null&&min.getId() > spr.getRegisteredSince().getId()){
    			min = spr.getRegisteredSince().toDto();
    		}
    	}

    	if(min.getId().longValue()==Long.MAX_VALUE){
    		return null;
    	}
    	return min;
        
    }

    private List<CuorseRegistration> courseRegistrations(SemesterDto semester, SemesterDto currentSemester, List<Lehrveranstaltung> courses, List<Grade> grades, List<Feedback> feedbacks) {
    	return courses.stream()
                .filter(it -> it.getSemester().toDto().equals(semester))
                .map(it -> new CuorseRegistration(it, courseRegistrationState(it, currentSemester, grades, feedbacks), courseGrade(grades, it)))
                .collect(Collectors.toList());
    }

    private CourseRegistrationState courseRegistrationState(Lehrveranstaltung course, SemesterDto currentSemester, List<Grade> grades, List<Feedback> feedbacks) {
        Optional<Grade> grade = grades.stream().filter(it -> it.getCourse().equals(course)).findFirst();
        Optional<Feedback> feedback = feedbacks.stream().filter(it -> it.getCourse().equals(course)).findFirst();

        if (feedback.isPresent() && grade.isPresent() && grade.isPresent()) {
            return grade.get().getMark().isPositive() ? CourseRegistrationState.complete_ok : CourseRegistrationState.complete_not_ok;
        } else if (feedback.isPresent()) {
            return CourseRegistrationState.needs_grade;
        } else if (grade.isPresent()) {
            return CourseRegistrationState.needs_feedback;
        } else {
            return course.getSemester().toDto().equals(currentSemester) ? CourseRegistrationState.in_progress : CourseRegistrationState.needs_feedback;
        }
    }
    
    private Grade courseGrade(List<Grade> grades, Lehrveranstaltung course){
    	Optional<Grade> grade = grades.stream().filter(it -> it.getCourse().equals(course)).findFirst();
    	if(grade.isPresent()){
    		return grade.get();
    	}else{
    		return null;
    	}
    }
}
