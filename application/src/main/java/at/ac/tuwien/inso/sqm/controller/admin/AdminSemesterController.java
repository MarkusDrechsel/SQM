package at.ac.tuwien.inso.sqm.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import at.ac.tuwien.inso.sqm.service.SemesterServiceInterface;

@Controller
@RequestMapping("/admin/semester")
public class AdminSemesterController {

    @Autowired
    private SemesterServiceInterface semesterService;

    @GetMapping
    public String semester(Model model) {
        model.addAttribute("currentSemester", semesterService.getOrCreateCurrentSemester());
        model.addAttribute("allSemesters", semesterService.findAll());
        return "admin/semester";
    }
}
