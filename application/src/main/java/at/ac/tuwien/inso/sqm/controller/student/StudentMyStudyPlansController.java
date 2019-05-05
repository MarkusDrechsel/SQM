package at.ac.tuwien.inso.sqm.controller.student;

import java.util.List;
import java.util.stream.Collectors;

import at.ac.tuwien.inso.sqm.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.service.StudentServiceInterface;
import at.ac.tuwien.inso.sqm.service.StudyPlanService;
import at.ac.tuwien.inso.sqm.service.UserAccountService;

@Controller
@RequestMapping("/student/my-studyplans")
public class StudentMyStudyPlansController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private StudentServiceInterface studentService;

    @Autowired
    private StudyPlanService studyPlanService;

    @GetMapping
    public String listStudyPlansForStudent() {
        return "/student/my-studyplans";
    }

    @ModelAttribute("studyPlanRegistrations")
    private Iterable<StudyPlanRegistration> getStudyPlans() {
        StudentEntity student = studentService.findOne(userAccountService.getCurrentLoggedInUser());
        return student.getStudyplans();
    }

    @GetMapping(params = "id")
    private String getStudyplanDetailsView(@RequestParam(value = "id") Long id, Model model) {
        StduyPlanEntity studyPlan = studyPlanService.findOne(id);

        List<SubjectWithGrade> subjectsForStudyPlanWithGrades = studyPlanService.getSubjectsWithGradesForStudyPlan(id);

        // filter by subject type: mandatory, optional or free choice
        List<SubjectWithGrade> mandatory = subjectsForStudyPlanWithGrades
                .stream()
                .filter(s -> s.getSubjectType().equals(SubjectTyppe.mandatory))
                .collect(Collectors.toList());
        List<SubjectWithGrade> optional = subjectsForStudyPlanWithGrades
                .stream()
                .filter(s -> s.getSubjectType().equals(SubjectTyppe.optional))
                .collect(Collectors.toList());
        List<SubjectWithGrade> freeChoice = subjectsForStudyPlanWithGrades
                .stream()
                .filter(s -> s.getSubjectType().equals(SubjectTyppe.FREE_CHOICE))
                .collect(Collectors.toList());

        model.addAttribute("studyPlan", studyPlan);
        model.addAttribute("mandatory", mandatory);
        model.addAttribute("optional", optional);
        model.addAttribute("freeChoice", freeChoice);

        // calculate study progress
        double progressMandatory = mandatory
                .stream()
                .filter(s -> s.getGrade() != null && s.getGrade().getMark().isPositive())
                .mapToDouble(s -> s.getSubjectForStudyPlan().getSubject().getEcts().doubleValue())
                .sum();

        double progressOptional = optional
                .stream()
                .filter(s -> s.getGrade() != null && s.getGrade().getMark().isPositive())
                .mapToDouble(s -> s.getSubjectForStudyPlan().getSubject().getEcts().doubleValue())
                .sum();

        double progressFreeChoice = freeChoice
                .stream()
                .filter(s -> s.getGrade().getMark().isPositive())
                .mapToDouble(s -> s.getGrade().getCourse().getSubject().getEcts().doubleValue())
                .sum();

        model.addAttribute("progressMandatory", progressMandatory);
        model.addAttribute("progressOptional", progressOptional);
        model.addAttribute("progressFreeChoice", progressFreeChoice);

        return "student/my-studyplan-details";
    }


}
