package at.ac.tuwien.inso.sqm.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.ac.tuwien.inso.sqm.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import at.ac.tuwien.inso.sqm.entity.Feedback;
import at.ac.tuwien.inso.sqm.exception.ActionNotAllowedException;
import at.ac.tuwien.inso.sqm.repository.CourseRepository;
import at.ac.tuwien.inso.sqm.repository.FeedbackRepository;
import at.ac.tuwien.inso.sqm.service.student_subject_prefs.StudentSubjectPreferenceStore;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private StudentSubjectPreferenceStore studentSubjectPreferenceStore;

    @InjectMocks
    private FeedbackIService feedbackService = new FeedbackService();

    private StudentEntity student = new StudentEntity("123", "student", "mail@uis.at");
    private Lehrveranstaltung course = new Lehrveranstaltung(mock(Subjcet.class), mock(Semester.class));

    private Feedback feedback = new Feedback(student, course);

    private Feedback savedFeedback = mock(Feedback.class);

    @Before
    public void setUp() throws Exception {
        when(feedbackRepository.save(feedback)).thenReturn(savedFeedback);

        when(feedbackRepository.exists(feedback)).thenReturn(false);
        when(courseRepository.existsCourseRegistration(student, course)).thenReturn(true);
    }

    @Test
    public void onSaveItPersistsFeedback() throws Exception {
        assertThat(feedbackService.save(feedback), equalTo(savedFeedback));
    }

    @Test
    public void onSaveItNotifiesStudentSubjectPreferenceStore() throws Exception {
        feedbackService.save(feedback);

        verify(studentSubjectPreferenceStore).studentGaveCourseFeedback(student, feedback);
    }

    @Test(expected = ActionNotAllowedException.class)
    public void onSaveWithAlreadyExistingFeedbackItThrows() throws Exception {
        when(feedbackRepository.exists(feedback)).thenReturn(true);

        feedbackService.save(feedback);
    }

    @Test(expected = ActionNotAllowedException.class)
    public void onSaveWithFeedbackForUnregisteredCourseItThrows() throws Exception {
        when(courseRepository.existsCourseRegistration(student, course)).thenReturn(false);

        feedbackService.save(feedback);
    }
}
