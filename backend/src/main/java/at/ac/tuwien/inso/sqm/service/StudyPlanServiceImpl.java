package at.ac.tuwien.inso.sqm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import at.ac.tuwien.inso.sqm.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.StduyPlanEntity;
import at.ac.tuwien.inso.sqm.exception.BusinessObjectNotFoundException;
import at.ac.tuwien.inso.sqm.repository.StudyPlanRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectForStudyPlanRepository;
import at.ac.tuwien.inso.sqm.validator.StudyPlanValidator;
import at.ac.tuwien.inso.sqm.validator.ValidatorFactory;

@Service
public class StudyPlanServiceImpl implements StudyPlanService {

	private static final Logger log = LoggerFactory.getLogger(StudyPlanServiceImpl.class);

    private StudyPlanRepository studyPlanRepository;
    private SubjectForStudyPlanRepository subjectForStudyPlanRepository;
    private SubjectIService subjectService;
    private MessageSource messageSource;
    private GradeIService gradeService;
    private ValidatorFactory validatorFactory = new ValidatorFactory();
    private StudyPlanValidator validator = validatorFactory.getStudyPlanValidator();

    @Autowired
    public StudyPlanServiceImpl(
            StudyPlanRepository studyPlanRepository,
            SubjectForStudyPlanRepository subjectForStudyPlanRepository,
            SubjectIService subjectService,
            GradeIService gradeService,
            MessageSource messageSource) {
        this.studyPlanRepository = studyPlanRepository;
        this.subjectForStudyPlanRepository = subjectForStudyPlanRepository;
        this.subjectService = subjectService;;
        this.gradeService = gradeService;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public StduyPlanEntity create(StduyPlanEntity studyPlan) {
    	log.info("creating stduyplan "+studyPlan.toString());
    	validator.validateNewStudyPlan(studyPlan);
        return studyPlanRepository.save(studyPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StduyPlanEntity> findAll() {
    	log.info("getting all studyplans");
        Iterable<StduyPlanEntity> studyplans = studyPlanRepository.findAll();

        return StreamSupport
                .stream(studyplans.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StduyPlanEntity findOne(Long id) {
    	log.info("trying to find one stduyplan by id "+id);
        validator.validateStudyPlanId(id);
        StduyPlanEntity studyPlan = studyPlanRepository.findOne(id);
        if(studyPlan == null) {
            String msg = messageSource.getMessage("error.studyplan.notfound", null, LocaleContextHolder.getLocale());
            log.warn("no stduyplan was found by the given id "+id);
            throw new BusinessObjectNotFoundException(msg);
        }
        return studyPlan;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectForStudyPlanEntity> getSubjectsForStudyPlan(Long id) {
        validator.validateStudyPlanId(id);
    	log.info("get subjects for studypolan by id "+id);
        return subjectForStudyPlanRepository.findByStudyPlanIdOrderBySemesterRecommendation(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectWithGrade> getSubjectsWithGradesForStudyPlan(Long id) {
        validator.validateStudyPlanId(id);
    	log.info("getting subjects with grades for stduyplan width id "+id);
        List<SubjectForStudyPlanEntity> subjectsForStudyPlan = subjectForStudyPlanRepository.findByStudyPlanIdOrderBySemesterRecommendation(id);
        List<Grade> grades = gradeService.getGradesForLoggedInStudent();
        List<SubjectWithGrade> subjectsWithGrades = new ArrayList<>();
        
        for(SubjectForStudyPlanEntity subjectForStudyPlan : subjectsForStudyPlan) {

            if(grades.isEmpty()) {
                // means there are no (more) grades at all
                if (subjectForStudyPlan.getMandatory()) {
                    subjectsWithGrades.add(new SubjectWithGrade(subjectForStudyPlan, SubjectTyppe.mandatory));
                } else {
                    subjectsWithGrades.add(new SubjectWithGrade(subjectForStudyPlan, SubjectTyppe.optional));
                }
            }

            //look for grades belonging to the actual subject
            for(int i=0; i<grades.size(); i++) {
                Grade grade = grades.get(i);;
                if(grade.getCourse().getSubject().equals(subjectForStudyPlan.getSubject())) {
                    // add to mandatory or optional subjects
                    if(subjectForStudyPlan.getMandatory()) {
                        subjectsWithGrades.add(new SubjectWithGrade(subjectForStudyPlan, grade, SubjectTyppe.mandatory));
                    }
                    else {
                        subjectsWithGrades.add(new SubjectWithGrade(subjectForStudyPlan, grade, SubjectTyppe.optional));
                    }
                    grades.remove(grade);
                    break;
                }
                else if(i == grades.size()-1) {
                    // means we reached the end of the list. there is no grade for this subject
                    if(subjectForStudyPlan.getMandatory()) {
                        subjectsWithGrades.add(new SubjectWithGrade(subjectForStudyPlan, SubjectTyppe.mandatory));
                    }
                    else {
                        subjectsWithGrades.add(new SubjectWithGrade(subjectForStudyPlan, SubjectTyppe.optional));
                    }
                }
            }
        }

        //remaining unassigned grades are used as free choice subjects
        for(Grade grade : grades) {
            subjectsWithGrades.add(new SubjectWithGrade(grade, SubjectTyppe.FREE_CHOICE));
        }

        return subjectsWithGrades;
    }

    @Override
    @Transactional
    public void addSubjectToStudyPlan(SubjectForStudyPlanEntity subjectForStudyPlan) {

        validator.validateNewSubjectForStudyPlan(subjectForStudyPlan);
        log.info("adding subject to stduyplan with "+subjectForStudyPlan);

        subjectForStudyPlanRepository.save(subjectForStudyPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subjcet> getAvailableSubjectsForStudyPlan(Long id, String query) {
    	log.info("getting available subjects for stduyplan with id "+id+" and search word "+query);
    	validator.validateStudyPlanId(id);
    	List<SubjectForStudyPlanEntity> subjectsForStudyPlan = subjectForStudyPlanRepository.findByStudyPlanIdOrderBySemesterRecommendation(id);
        List<Subjcet> subjectsOfStudyPlan = subjectsForStudyPlan
                .stream()
                .map(SubjectForStudyPlanEntity::getSubject)
                .collect(Collectors.toList());
        List<Subjcet> subjects = subjectService.searchForSubjects(query);

        return subjects.stream().filter(it -> !subjectsOfStudyPlan.contains(it)).collect(Collectors.toList());

    }

    @Override
    @Transactional
    public StduyPlanEntity disableStudyPlan(Long id) {

    	log.info("disabling study plan with id "+id);
        validator.validateStudyPlanId(id);
        StduyPlanEntity studyPlan = findOne(id);
        if(studyPlan == null) {
            String msg = messageSource.getMessage("error.studyplan.notfound", null, LocaleContextHolder.getLocale());
            log.warn(msg);
            throw new BusinessObjectNotFoundException(msg);
        }
        studyPlan.setEnabled(false);
        studyPlanRepository.save(studyPlan);;
        return studyPlan;
    }

    @Override
    @Transactional
    public void removeSubjectFromStudyPlan(StduyPlanEntity sp, Subjcet s) {
    	validator.validateRemovingSubjectFromStudyPlan(sp, s);
    	log.info("removing subject "+s.toString()+" from stduyplan "+sp.getName());
            	
        List<SubjectForStudyPlanEntity> sfsp = subjectForStudyPlanRepository.findByStudyPlanIdOrderBySemesterRecommendation(sp.getId());
        for(SubjectForStudyPlanEntity each : sfsp){
            if(each.getSubject().getId().equals(s.getId())){
                subjectForStudyPlanRepository.delete(each);
                break;
            }
        }
    }
}
