package at.ac.tuwien.inso.sqm.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import at.ac.tuwien.inso.sqm.entity.SemestreTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SemesterDtoTest {

	private SemesterDto getWS2016WithoutId() {
		return new SemesterDto(2016, SemestreTypeEnum.WinterSemester);
	}

	private SemesterDto getWS2016(){
		SemesterDto a = getWS2016WithoutId();
		a.setId(1L);
		return a;
	}
	
	private SemesterDto getSS2015(){
		SemesterDto b = new SemesterDto(2015, SemestreTypeEnum.SummerSemester);
		b.setId(2L);
		return b;
	}

	
	@Test
	public void testEquals() {
		assertTrue(getWS2016().equals(getWS2016()));
	}
	
	@Test
	public void testEqualsDifferntValues() {
		assertFalse(getWS2016().equals(getSS2015()));
	}

	@Test
	public void testIsStartInPast() {
        Calendar before = new GregorianCalendar(2000, 1, 1);
        Calendar after = new GregorianCalendar(2020, 3, 3);

        SemesterDto semester = new SemesterDto(2010, SemestreTypeEnum.SummerSemester);

        assertFalse(semester.isStartInPast(before));
        assertTrue(semester.isStartInPast(after));
    }

	@Test
	public void testCalculateCurrentSemesterSS() {
		Calendar currentDay = new GregorianCalendar(2016, 06, 06);

		SemesterDto expected = new SemesterDto(2016, SemestreTypeEnum.SummerSemester);

		SemesterDto actual = SemesterDto.calculateCurrentSemester(currentDay);

		assertEquals(expected, actual);
	}

	@Test
	public void testCalculateCurrentSemesterWS() {
		Calendar currentDay = new GregorianCalendar(2000, 01, 07);

		SemesterDto expected = new SemesterDto(1999, SemestreTypeEnum.WinterSemester);

		SemesterDto actual = SemesterDto.calculateCurrentSemester(currentDay);

		assertEquals(expected, actual);
	}

    @Test
    public void testIsCurrent() {
        Calendar now = new GregorianCalendar(2016, 1, 1);

        SemesterDto semester = new SemesterDto(2015, SemestreTypeEnum.WinterSemester);

        assertTrue(semester.isCurrent(now));
    }

    @Test
    public void testNotIsCurrent() {
        Calendar now = new GregorianCalendar(2000, 7, 7);

        SemesterDto semester = new SemesterDto(2000, SemestreTypeEnum.WinterSemester);

        assertFalse(semester.isCurrent(now));
    }

    @Test
    public void testNotIsCurrentOtherYear() {
        Calendar now = new GregorianCalendar(2000, 7, 7);

        SemesterDto semester = new SemesterDto(2010, SemestreTypeEnum.SummerSemester);

        assertFalse(semester.isCurrent(now));
    }

    @Test
    public void testNextPossibleSemester() {
	    SemesterDto semester = new SemesterDto(2016, SemestreTypeEnum.WinterSemester);

	    SemesterDto expected = new SemesterDto(2017, SemestreTypeEnum.SummerSemester);

	    SemesterDto actual = semester.nextSemester();

	    assertEquals(expected, actual);
    }

}
