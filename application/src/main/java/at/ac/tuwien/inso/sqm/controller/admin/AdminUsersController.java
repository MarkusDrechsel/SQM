package at.ac.tuwien.inso.sqm.controller.admin;

import static at.ac.tuwien.inso.sqm.controller.Constants.MAX_PAGE_SIZE;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.validation.Valid;

import at.ac.tuwien.inso.sqm.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import at.ac.tuwien.inso.sqm.controller.admin.forms.CreateUserForm;
import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import at.ac.tuwien.inso.sqm.exception.ValidationException;
import at.ac.tuwien.inso.sqm.service.LecturerService;
import at.ac.tuwien.inso.sqm.service.StudentServiceInterface;
import at.ac.tuwien.inso.sqm.service.UisUserServiceInterface;
import at.ac.tuwien.inso.sqm.service.UserCreationService;

@Controller
@RequestMapping("/admin/users")
public class AdminUsersController {

    @Autowired
    private UisUserServiceInterface uisUserService;

    @Autowired
    private StudentServiceInterface studentService;

    @Autowired
    private LecturerService lecturerService;

    @Autowired
    private UserCreationService userCreationService;

    @GetMapping
    public String listUsers(
            @RequestParam(value = "search", required = false) String search,
            Model model
    ) {
        if ("".equals(search)) {
            return "redirect:/admin/users";
        }

        return listUsersInternal(search, 1, model);
    }

    @GetMapping("/page/{pageNumber}")
    public String listUsersForPage(
            @RequestParam(value = "search", required = false) String search,
            @PathVariable Integer pageNumber,
            Model model
    ) {
        if (search == null && pageNumber == 1) {
            return "redirect:/admin/users";
        }

        if ("".equals(search)) {
            return "redirect:/admin/users/page/" + pageNumber;
        }

        if (pageNumber == 1) {
            return "redirect:/admin/users?search=" + search;
        }

        return listUsersInternal(search, pageNumber, model);
    }

    private String listUsersInternal(String search, int pageNumber, Model model) {
        if (search == null) {
            search = "";
        }

        // Page numbers in the URL start with 1
        PageRequest pageable = new PageRequest(pageNumber - 1, MAX_PAGE_SIZE);

        Page<UisUserEntity> usersPage = uisUserService.findAllMatching(search, pageable);
        List<UisUserEntity> users = usersPage.getContent();

        // If the user tries to access a page that doesn't exist
        if (users.size() == 0 && usersPage.getTotalElements() != 0) {
            int lastPage = usersPage.getTotalPages();
            return "redirect:/admin/users/page/" + lastPage + "?search=" + search;
        }

        model.addAttribute("page", usersPage);
        model.addAttribute("search", search);

        return "admin/users";
    }

    @GetMapping("/{userId}")
    public String userDetails(@PathVariable Long userId, Model model) throws UnsupportedEncodingException {
        UisUserEntity user = uisUserService.findOne(userId);
        model.addAttribute("user", user);

        if (user instanceof StudentEntity) {
            Iterable<StudyPlanRegistration> studyplans = studentService.findStudyPlanRegistrationsFor((StudentEntity) user);
            model.addAttribute("studyplans", studyplans);

            return "admin/student-details";
        }

        Iterable<Subjcet> subjects = lecturerService.findSubjectsFor((LecturerEntity) user);
        model.addAttribute("subjects", subjects);
        String qr = lecturerService.generateQRUrl((LecturerEntity) user);
        model.addAttribute("qr", qr);

        return "admin/lecturer-details";
    }

    @PostMapping("/create")
    public String createUser(@Valid CreateUserForm form,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("flashMessage", "admin.users.create.error." + bindingResult.getFieldError().getField());

            return "redirect:/admin/users";
        }
        boolean error = false;
        try{
        	userCreationService.create(form.toUisUser());
        }catch(ValidationException e){
        	error = true;
            redirectAttributes.addFlashAttribute("flashMessage", "admin.users.create.error.email");
        }
        if(!error){
        	redirectAttributes.addFlashAttribute("flashMessage", "admin.users.create.success");
        }
        
        return "redirect:/admin/users";
    }
}
