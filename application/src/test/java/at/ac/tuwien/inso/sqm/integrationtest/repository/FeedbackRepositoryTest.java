package at.ac.tuwien.inso.sqm.integrationtest.repository;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.FeedbackRepository;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;
import at.ac.tuwien.inso.sqm.repository.StduentRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class FeedbackRepositoryTest {

    @Autowired
    private StduentRepository stduentRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SemestreRepository semesterRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    private List<StudentEntity> students;

    private Lehrveranstaltung course;

    @Before
    public void setUp() throws Exception {
        prepareStudents();
        prepareCourse();
    }

    private void prepareStudents() {
        students = StreamSupport.stream(stduentRepository.save(asList(
                new StudentEntity("123", "student", "student123@uis.at"),
                new StudentEntity("456", "student", "student456@uis.at")
        )).spliterator(), false).collect(Collectors.toList());
    }

    private void prepareCourse() {
        Subjcet subject = subjectRepository.save(new Subjcet("subject", BigDecimal.ONE));
        Semester semester = semesterRepository.save(new Semester(2016, SemestreTypeEnum.SummerSemester));

        course = courseRepository.save(new Lehrveranstaltung(subject, semester));
    }

    @Test
    public void findAllOfStudentWithEmptyFeedbackEntries() throws Exception {
        prepareFeedbackFor(students.get(0));

        List<Feedback> feedbackEntries = feedbackRepository.findAllOfStudent(students.get(1));

        assertThat(feedbackEntries, empty());
    }

    private Feedback prepareFeedbackFor(StudentEntity student) {
        return feedbackRepository.save(new Feedback(student, course));
    }

    @Test
    public void findAllOfStudentWithSomeFeedbackEntries() throws Exception {
        Feedback feedback = prepareFeedbackFor(students.get(0));

        List<Feedback> feedbackEntries = feedbackRepository.findAllOfStudent(students.get(0));

        assertThat(feedbackEntries, equalTo(singletonList(feedback)));
    }

    @Test
    public void testExistsForExistentFeedback() throws Exception {
        Feedback feedback = prepareFeedbackFor(students.get(0));

        assertTrue(feedbackRepository.exists(feedback));
    }

    @Test
    public void testExistsForNonExistentFeedback() throws Exception {
        Feedback feedback = new Feedback(students.get(0), course);

        assertFalse(feedbackRepository.exists(feedback));
    }
}
