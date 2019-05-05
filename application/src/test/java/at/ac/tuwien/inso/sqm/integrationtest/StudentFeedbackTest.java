package at.ac.tuwien.inso.sqm.integrationtest;

import static at.ac.tuwien.inso.sqm.entity.Feedback.Type.DISLIKE;
import static at.ac.tuwien.inso.sqm.entity.Feedback.Type.LIKE;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.controller.student.forms.FeedbackForm;
import at.ac.tuwien.inso.sqm.entity.Feedback;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.FeedbackRepository;
import at.ac.tuwien.inso.sqm.repository.GradeRepository;
import at.ac.tuwien.inso.sqm.repository.LecturerRepository;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.repository.StduentRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectRepository;
import at.ac.tuwien.inso.sqm.repository.UisUserRepository;
import at.ac.tuwien.inso.sqm.service.student_subject_prefs.StudentSubjectPreferenceStore;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class StudentFeedbackTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UisUserRepository uisUserRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SemestreRepository semesterRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private StduentRepository stduentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private LecturerRepository lecturerRepository;
    @Autowired
    private StudentSubjectPreferenceStore studentSubjectPreferenceStore;

    private StudentEntity student;
    private List<Lehrveranstaltung> courses;

    @Before
    public void setUp() {
        prepareStudent();
        prepareCourses();
    }

    private void prepareStudent() {
        student = uisUserRepository.save(new StudentEntity("1", "student", "email", new UserAccountEntity("student", "pass", Rolle.STUDENT)));
    }

    private void prepareCourses() {
        Subjcet subject = subjectRepository.save(new Subjcet("subject", BigDecimal.ONE));
        Semester semester = semesterRepository.save(new Semester(2016, SemestreTypeEnum.WinterSemester));

        courses = StreamSupport.stream(courseRepository.save(asList( new Lehrveranstaltung(subject, semester), new Lehrveranstaltung(subject, semester).addStudents(student))).spliterator(), false).collect(Collectors.toList());

        studentSubjectPreferenceStore.studentRegisteredCourse(student, courses.get(1));
    }

    @Test
    public void itPersistsFeedbackFromStudentForCourseHeIsRegisteredTo() throws Exception {
        FeedbackForm form = new FeedbackForm(courses.get(1).getId(), false, "some description");

        mockMvc.perform(
                giveFeedback(form)
        ).andExpect(
                feedbackCreated(form)
        ).andExpect(
                redirectedUrl("/student/meineLehrveranstaltungen")
        ).andExpect(
                flash().attribute("flashMessage", "student.my.courses.feedback.success")
        );
    }

    private MockHttpServletRequestBuilder giveFeedback(FeedbackForm form) {
        return post("/student/feedback")
                .param("course", form.getCourse().toString())
                .param("suggestions", form.getSuggestions())
                .param("like", form.isLike().toString())
                .with(csrf())
                .with(user(student.getAccount()));
    }

    private ResultMatcher feedbackCreated(FeedbackForm form) {
        return result -> {
            List<Feedback> feedbacks = feedbackRepository.findAllOfStudent(student);
            assertThat(feedbacks, hasSize(1));

            Feedback feedback = feedbacks.get(0);
            assertThat(feedback.getSuggestions(), equalTo(form.getSuggestions()));
            assertThat(feedback.getType(), equalTo(form.isLike() ? LIKE : DISLIKE));
        };
    }

    @Test
    public void itDoesNotPersistsFeedbackFromStudentForCourseHeIsNotRegisteredTo() throws Exception {
        FeedbackForm form = new FeedbackForm(courses.get(0).getId(), true, "some description");

        mockMvc.perform(
                giveFeedback(form)
        ).andExpect(
                feedbackNotCreated()
        ).andExpect(
                status().isForbidden()
        );
    }

    private ResultMatcher feedbackNotCreated() {
        return result -> assertThat(feedbackRepository.findAllOfStudent(student), empty());
    }

    @Test
    public void itRespondsNotFoundOnFeedbackForUnknownCourse() throws Exception {
        FeedbackForm form = new FeedbackForm(-1L, false, "some description");

        mockMvc.perform(
                giveFeedback(form)
        ).andExpect(
                status().isNotFound()
        );
    }

    @Test
    public void itRespondsForbiddenOnMultipleFeedbackSubmissionsForSameCourse() throws Exception {
        feedbackRepository.save(new Feedback(student, courses.get(1)));

        FeedbackForm form = new FeedbackForm(courses.get(1).getId(), true, "some description");

        mockMvc.perform(
                giveFeedback(form)
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    public void itDoesNotPersistFeedbackWithTooLongSuggestions() throws Exception {
        FeedbackForm form = new FeedbackForm(courses.get(1).getId(), true, tooLongSuggestions());

        mockMvc.perform(
                giveFeedback(form)
        ).andExpect(
                feedbackNotCreated()
        ).andExpect(
                status().isBadRequest()
        );
    }

    private String tooLongSuggestions() {
        StringBuilder builder = new StringBuilder();
        IntStream.range(0, 1025).forEach(it -> builder.append("a"));
        return builder.toString();
    }
}
