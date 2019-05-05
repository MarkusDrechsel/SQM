package at.ac.tuwien.inso.sqm.controller.lecturer;

import at.ac.tuwien.inso.sqm.entity.Subjcet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import at.ac.tuwien.inso.sqm.service.LecturerService;

@Controller
@RequestMapping("/lecturer/subjects")
public class LecturerSubjectsController {

    @Autowired
    private LecturerService lecturerService;

    @GetMapping
    public String subjects() {
        return "lecturer/subjects";
    }

    @ModelAttribute("ownedSubjects")
    private Iterable<Subjcet> getOwnSubjects() {
        return lecturerService.getOwnSubjects();
    }

}