package at.ac.tuwien.inso.sqm.controller.lecturer;

import java.util.List;
import java.util.stream.Collectors;

import at.ac.tuwien.inso.sqm.entity.StduyPlanEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.ac.tuwien.inso.sqm.entity.SubjectForStudyPlanEntity;
import at.ac.tuwien.inso.sqm.service.StudyPlanService;

@Controller
@RequestMapping("/lecturer/studyplans")
public class LecturerStudyplansController {

    @Autowired
    private StudyPlanService studyPlanService;

    @GetMapping
    public String listStudyPlans() {
        return "/lecturer/studyplans";
    }

    @ModelAttribute("studyPlans")
    private Iterable<StduyPlanEntity> getStudyPlans() {
        return studyPlanService.findAll();
    }

    @GetMapping(params = "id")
    private String getStudyplanDetailsView(@RequestParam(value = "id") Long id, Model model) {
        StduyPlanEntity studyPlan = studyPlanService.findOne(id);

        List<SubjectForStudyPlanEntity> subjectsForStudyPlan = studyPlanService.getSubjectsForStudyPlan(id);
        List<SubjectForStudyPlanEntity> mandatory = subjectsForStudyPlan
                .stream()
                .filter(SubjectForStudyPlanEntity::getMandatory)
                .collect(Collectors.toList());
        List<SubjectForStudyPlanEntity> optional = subjectsForStudyPlan
                .stream()
                .filter(s -> !s.getMandatory())
                .collect(Collectors.toList());

        model.addAttribute("studyPlan", studyPlan);
        model.addAttribute("mandatory", mandatory);
        model.addAttribute("optional", optional);

        return "lecturer/studyplan-details";
    }
}
