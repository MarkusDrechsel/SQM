package at.ac.tuwien.inso.sqm.integrationtest;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.controller.admin.forms.CreateStudyPlanForm;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.repository.StduentRepository;
import at.ac.tuwien.inso.sqm.repository.StudyPlanRepository;
import at.ac.tuwien.inso.sqm.repository.UisUserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminStudyPlansTest extends AbstractStudyPlansTest {

    @Autowired
    private UisUserRepository uisUserRepository;

    @Autowired
    private SemestreRepository semesterRepository;

    @Autowired
    private StduentRepository stduentRepository;

    @Autowired
    private StudyPlanRepository studyplanRepository;


    @Test
    public void adminShouldSeeAllStudyPlansTest() throws Exception {

        // given study plans
        studyPlanRepository.save(asList(studyPlan1, studyPlan2, studyPlan3));

        // the admin should see them all
        mockMvc.perform(
                get("/admin/studyplans").with(user("admin").roles("ADMIN"))
        ).andExpect(
                model().attribute("studyPlans", asList(studyPlan1, studyPlan2, studyPlan3))
        );
    }

    @Test
    public void adminShouldSeeDetailsOfStudyPlanTest() throws Exception {

        // given subjects in a study plan
        StduyPlanEntity studyPlan = studyPlanRepository.save(studyPlan1);

        // the admin should see details of the study plan, containing separate lists of mandatory and optional subjects
        mockMvc.perform(
                get("/admin/studyplans").param("id", studyPlan.getId().toString()).with(user("admin").roles("ADMIN"))
        ).andExpect(
                model().attribute("studyPlan", studyPlan)
        ).andExpect(
                model().attribute("mandatory", asList(s1, s2, s3, s6))
        ).andExpect(
                model().attribute("optional", asList(s5, s4))
        );
    }

    @Test
    public void studyPlanSuccessCreationTest() throws Exception {

        // when the form is filled correctly
        CreateStudyPlanForm form = new CreateStudyPlanForm("Bachelor SE", new BigDecimal(120.0), new BigDecimal(30.0), new BigDecimal(30.0));
        StduyPlanEntity expectedStudyPlan = form.toStudyPlan();

        // the created study plan should be seen
        createStudyPlan(form)
                .andExpect(view().name("admin/studyplan-details"))
                .andExpect(model().attribute("studyPlan", expectedStudyPlan));

        // and integrationtest should be persisted
        List<StduyPlanEntity> allStudyPlans = StreamSupport.stream(studyPlanRepository.findAll().spliterator(), false).collect(Collectors.toList());
        assertTrue(allStudyPlans.contains(expectedStudyPlan));
    }

    @Test
    public void studyPlanFailureCreationNameIsEmptyTest() throws Exception {

        // when the form is filled not correctly
        CreateStudyPlanForm form = new CreateStudyPlanForm("", new BigDecimal(120.0), new BigDecimal(30.0), new BigDecimal(30.0));
        StduyPlanEntity wrongStudyPlan = form.toStudyPlan();

        // nothing should be persisted
        List<StduyPlanEntity> allStudyPlans = StreamSupport.stream(studyPlanRepository.findAll().spliterator(), false).collect(Collectors.toList());
        assertFalse(allStudyPlans.contains(wrongStudyPlan));
    }

    @Test
    public void studyPlanFailureCreationEctsNegativeTest() throws Exception {

        // when the form is filled not correctly
        CreateStudyPlanForm form = new CreateStudyPlanForm("Bachelor SE", new BigDecimal(-120.0), new BigDecimal(-30.0), new BigDecimal(-30.0));
        StduyPlanEntity wrongStudyPlan = form.toStudyPlan();

        // nothing should be persisted
        List<StduyPlanEntity> allStudyPlans = StreamSupport.stream(studyPlanRepository.findAll().spliterator(), false).collect(Collectors.toList());
        assertFalse(allStudyPlans.contains(wrongStudyPlan));
    }

    @Test
    public void studyPlanFailureCreationStudyPlanAlreadyExistsTest() throws Exception {

        // given study plans
        studyPlanRepository.save(asList(studyPlan1, studyPlan2, studyPlan3));

        // when trying to create the same study plan again
        CreateStudyPlanForm form = new CreateStudyPlanForm(studyPlan1.getName(), new BigDecimal(120.0), new BigDecimal(30.0), new BigDecimal(30.0));
        StduyPlanEntity wrongStudyPlan = form.toStudyPlan();

        // nothing should be persisted
        List<StduyPlanEntity> allStudyPlans = StreamSupport.stream(studyPlanRepository.findAll().spliterator(), false).collect(Collectors.toList());
        assertFalse(allStudyPlans.contains(wrongStudyPlan));
    }

    @Test
    public void disableStudyPlanTest() throws Exception{
        StduyPlanEntity studyPlan = studyPlanRepository.save(studyPlan1);
        mockMvc.perform(
                post("/admin/studyplans/disable/")
                        .param("id", studyPlan.getId().toString())
                        .with(user("admin").roles(Rolle.ADMIN.name()))
                        .with(csrf())
        ).andExpect(
                (redirectedUrl("/admin/studyplans"))
        ).andExpect(it -> {
            StduyPlanEntity s = studyPlanRepository.findOne(studyPlan.getId());
            assertFalse(s.isEnabled());
        });
    }

    @Test
    public void addSubjectToStudyPlanSuccessTest() throws Exception {

        //given study plan and a subject which is not part of integrationtest
        StduyPlanEntity studyPlan = studyPlanRepository.save(studyPlan1);
        Subjcet subject = subjectRepository.save(new Subjcet("Operating Systems", new BigDecimal(4.0)));

        //when a mandatory subject is added to the study plan
        mockMvc.perform(
                post("/admin/studyplans/addSubject")
                        .with(user("admin").roles(Rolle.ADMIN.name()))
                        .param("subjectId", subject.getId().toString())
                        .param("studyPlanId", studyPlan.getId().toString())
                        .param("semester", "1")
                        .param("mandatory", "true")
                        .with(csrf())
        ).andExpect(
                (redirectedUrl("/admin/studyplans/?id="+studyPlan.getId()))
        );

        //the subject should be part of the mandatory subjects of the study plan
        List<SubjectForStudyPlanEntity> subjectsForStudyPlan = StreamSupport
                .stream(subjectForStudyPlanRepository.findByStudyPlanIdOrderBySemesterRecommendation(studyPlan.getId())
                        .spliterator(), false).collect(Collectors.toList());
        List<Subjcet> usedSubjects = subjectsForStudyPlan.stream()
                .filter(SubjectForStudyPlanEntity::getMandatory)
                .map(s -> subjectRepository.findById(s.getSubject().getId())).collect(Collectors.toList());

        assertTrue(usedSubjects.contains(subject));
    }

    @Test
    public void addNonExistingSubjectToStudyPlanFailureTest() throws Exception {

        //given study plan and a subject which is already part of integrationtest
        Subjcet subject = new Subjcet("Operating Systems", new BigDecimal(4.0));
        subject.setId(1337L);
        //studyPlan1.addSubjects(new SubjectForStudyPlanEntity(subject, studyPlan1, true));
        StduyPlanEntity studyPlan = studyPlanRepository.save(studyPlan1);

        //when this mandatory subject is added to the study plan
        mockMvc.perform(
                post("/admin/studyplans/addSubject")
                        .with(user("admin").roles(Rolle.ADMIN.name()))
                        .param("subjectId", subject.getId().toString())
                        .param("studyPlanId", studyPlan.getId().toString())
                        .param("semester", "1")
                        .param("mandatory", "true")
                        .with(csrf())
        ).andExpect(
                (redirectedUrl("/admin/studyplans/?id="+studyPlan.getId()))
        );

        //the subject should not be part of the mandatory subjects of the study plan
        List<SubjectForStudyPlanEntity> subjectsForStudyPlan = StreamSupport
                .stream(subjectForStudyPlanRepository.findByStudyPlanIdOrderBySemesterRecommendation(studyPlan.getId())
                        .spliterator(), false).collect(Collectors.toList());
        List<Subjcet> usedSubjects = subjectsForStudyPlan.stream()
                .filter(SubjectForStudyPlanEntity::getMandatory)
                .map(s -> subjectRepository.findById(s.getSubject().getId())).collect(Collectors.toList());

        assertFalse(usedSubjects.contains(subject));
    }

    /**
     *
     * Tests if a Subjcet can be removed from a study plan and that the browser redirects properly afterwards
     * @throws Exception
     */
    @Test
    public void removeSubjectFromStudyPlanTest() throws Exception{

        // given subjects in a study plan
        StduyPlanEntity studyPlan = studyPlanRepository.save(studyPlan2);
        SubjectForStudyPlanEntity s1 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(0), studyPlan, true, 1));
        SubjectForStudyPlanEntity s2 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(1), studyPlan, true, 1));

        mockMvc.perform(
                post("/admin/studyplans/remove/").with(user("admin").roles("ADMIN"))
                        .param("studyPlanId", studyPlan.getId().toString())
                        .param("subjectId", s2.getSubject().getId().toString())
                        .with(csrf())
        ).andExpect(
                (redirectedUrl("/admin/studyplans/?id="+studyPlan.getId().toString()))
        ).andExpect(it -> {
            List<SubjectForStudyPlanEntity> list = subjectForStudyPlanRepository.findByStudyPlanIdOrderBySemesterRecommendation(studyPlan.getId());
            assertFalse(list.contains(s2));
        });
    }

    @Test
    public void registerStudentToStudyPlanTest() throws Exception {

        // given a student and a study plan
        StudentEntity student = uisUserRepository.save(new StudentEntity("s12345", "student", "student@uis.at"));

        // when registering this student to the study plan
        mockMvc.perform(
                post("/admin/studyplans/studentAnmelden").with(user("admin").roles("ADMIN"))
                        .param("studyPlanId", studyPlan1.getId().toString())
                        .param("studentId", student.getId().toString())
                        .with(csrf())
        ).andExpect(
                (redirectedUrl("/admin/users/"+student.getId().toString()))
        ).andExpect(it -> {
            List<StduyPlanEntity> studyPlans = student.getStudyplans().stream().map(StudyPlanRegistration::getStudyplan).collect(Collectors.toList());
            assertTrue(studyPlans.contains(studyPlan1));
        });

    }

    @Test
    public void isStudyPlanAddedToStudent() throws Exception {
        Semester semester = semesterRepository.save(new Semester(2016, SemestreTypeEnum.WinterSemester));
        StudentEntity newStudent = stduentRepository.save(new StudentEntity("s1123456", "TestUser", "a@c.com"));

        StduyPlanEntity sp = studyplanRepository.save(new StduyPlanEntity("TestStudyPlan", new EtcsDistributionEntity(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN)));

        mockMvc.perform(
                post("/admin/studyplans/studentAnmelden/")
                        .param("studentId", newStudent.getId()+"")
                        .param("studyPlanId", sp.getId()+"")
                        .with(user("admin").roles(Rolle.ADMIN.name()))
                        .with(csrf())
        ).andExpect(
                redirectedUrl("/admin/users/"+newStudent.getId())
        ).andExpect(it -> {
            StudentEntity s = stduentRepository.findOne(newStudent.getId());
            assertEquals(sp, s.getStudyplans().get(0).getStudyplan());
        });
    }

    @Test
    public void availableSubjectsForStudyPlanJsonTest() throws Exception {

        // given 3 subjects, where all of them contain the string "Engineering", but one is already part of studyPlan2
        studyPlan1.addSubjects(new SubjectForStudyPlanEntity(subjects.get(1), studyPlan2, true));
        studyPlanRepository.save(studyPlan2);

        // when searching for "Engineering
        MvcResult result =  mockMvc.perform(
                get("/admin/studyplans/json/availableSubjects").with(user("admin").roles("ADMIN"))
                        .param("id", studyPlan2.getId().toString())
                        .param("query", "Engineering")
        ).andExpect((status().isOk())
        ).andReturn();

        // the other 2 subjects should be available for studyPlan2
        assertTrue(result.getResponse().getContentAsString().contains("Advanced Software Engineering"));
        assertTrue(result.getResponse().getContentAsString().contains("Model Engineering"));
        assertFalse(result.getResponse().getContentAsString().contains("Software Engineering and Project Management"));
    }

    @Test
    public void getRegisterStudentViewTest() throws Exception {

        // given study plans and a student registered to study plan 1
        Semester semester = semesterRepository.save(new Semester(2016, SemestreTypeEnum.SummerSemester));
        StudentEntity student = uisUserRepository.save(new StudentEntity("s12345", "student", "s12345@uis.at"));
        student.addStudyplans(new StudyPlanRegistration(studyPlan1, semester));
        studyPlanRepository.save(asList(studyPlan1, studyPlan2, studyPlan3));

        // when navigating to the register students page
        mockMvc.perform(
                get("/admin/studyplans/studentAnmelden").with(user("admin").roles("ADMIN"))
                .param("studentToAddId", student.getId().toString())
        ).andExpect(
                model().attribute("user", student)
        ).andExpect(
                model().attribute("studyPlans", asList(studyPlan2, studyPlan3))
        );

    }

    private ResultActions createStudyPlan(CreateStudyPlanForm form) throws Exception {
        return mockMvc.perform(
                post("/admin/studyplans/create").with(user("admin").roles(Rolle.ADMIN.name()))
                        .param("name", form.getName())
                        .param("mandatory", form.getMandatory().toString())
                        .param("optional", form.getOptional().toString())
                        .param("freeChoice", form.getFreeChoice().toString())
                        .with(csrf())
        );
    }
}
