package at.ac.tuwien.inso.sqm.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;

import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.entity.SemestreTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.entity.Semester;
import at.ac.tuwien.inso.sqm.exception.ValidationException;
import at.ac.tuwien.inso.sqm.repository.SubjectRepository;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class SubjectServiceTest {

	@Mock
	private LehrveranstaltungServiceInterface courseService;
	
	@Mock
	private SubjectRepository subjectRepository;
	
	@InjectMocks
    private SubjectIService subjectService = new SubjectService();
	
	@Test
	public void testRemoveWithValidInput(){
		Subjcet s = new Subjcet("testSubject", new BigDecimal(11));
		ArrayList<Lehrveranstaltung> toReturn = new ArrayList<>();
		when(courseService.findCoursesForSubject(s)).thenReturn(toReturn);

		assertTrue(subjectService.remove(s));

		
	}
	
	@Test(expected = ValidationException.class)
	public void testRemoveWithNullInputThrowsException() {
		subjectService.remove(null);
		
	}
	
	@Test(expected = ValidationException.class)
	public void testRemoveWithContainingCoursesInput() {

		Subjcet subject = new Subjcet("testSubject", new BigDecimal(11));

		Semester oldSemester = new Semester(2000, SemestreTypeEnum.WinterSemester);

		ArrayList<Lehrveranstaltung> toReturn = new ArrayList<>();
		toReturn.add(new Lehrveranstaltung(subject, oldSemester));

		when(courseService.findCoursesForSubject(subject)).thenReturn(toReturn);

        subjectService.remove(subject);
	}
}
