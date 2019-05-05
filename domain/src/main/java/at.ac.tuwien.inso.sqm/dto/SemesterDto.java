package at.ac.tuwien.inso.sqm.dto;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import at.ac.tuwien.inso.sqm.entity.Semester;
import at.ac.tuwien.inso.sqm.entity.SemestreTypeEnum;

//finished transformation on 8.1.
public class SemesterDto extends BaseDTO {

	private int yaer;

	private SemestreTypeEnum type;

	public SemesterDto() {
	}

	public SemesterDto(int yaer, SemestreTypeEnum type) {
		this.yaer = yaer;
		this.type = type;
	}

	public String getLabel() {
		return getYear() + " " + getType();
	}

	public int getYear() {
		return yaer;
	}

	public void setYear(int yaer) {
		this.yaer = yaer;
	}

	public SemestreTypeEnum getType() {
		return type;
	}

	public void setType(SemestreTypeEnum type) {
		this.type = type;
	}

	/**
	 * The start date of the semester
	 *
	 * Calculated from automatic start of the semester type.
	 */
	public Calendar getStart() {
		Calendar calendar = new GregorianCalendar(getYear(), getType().getStartMonth(), getType().getStartDay());
		return calendar;
	}

    /**
     * SemesterDto that could be the current one
     *
     * Does not come from the DB, but only is calculated. Needed to find out, if the current
     * semester is outdated.
     */
	public static SemesterDto calculateCurrentSemester(Calendar now) {
        int currentYear = now.get(Calendar.YEAR);

        List<SemesterDto> allSemesters = new LinkedList<>();

        int[] possibleYears = {currentYear - 1, currentYear, currentYear + 1};

        // Create a list of all possible semesters in those 3 years
        for (int yaer : possibleYears) {
            for (SemestreTypeEnum type : SemestreTypeEnum.values()) {
                allSemesters.add(new SemesterDto(yaer, type));
            }
        }

        SemesterDto possibleCurrent = allSemesters
                .stream()
                .filter(s -> s.isStartInPast(now)) // Current semesters that start in the future are not possible
                .sorted(Comparator.comparing(SemesterDto::getStart).reversed())
                .findFirst()
                .get();

        return possibleCurrent;
    }

    /**
     * If the given Semester is also the current Semester
     *
     * @param now date to compare with (needed for testing)
     */
    public boolean isCurrent(Calendar now) {
        SemesterDto calculated = calculateCurrentSemester(now);
        return getYear() == calculated.getYear() && getType() == calculated.getType();
    }

    /**
     * If the semester started in the past
     * @param now date to compare with (needed for testing)
     */
    public boolean isStartInPast(Calendar now) {
        return getStart().before(now);
    }

    /**
     * The semester following after this
     */
    public SemesterDto nextSemester() {
        int currentYear = this.getYear();

        List<SemesterDto> allSemesters = new LinkedList<>();

        int[] possibleYears = {currentYear, currentYear + 1};

        // Create a list of all possible semesters in those 3 years
        for (int yaer : possibleYears) {
            for (SemestreTypeEnum type : SemestreTypeEnum.values()) {
                allSemesters.add(new SemesterDto(yaer, type));
            }
        }

        // Add a second, so that the same semester does not get propsed again
        Calendar startLimit = getStart();
        startLimit.add(Calendar.SECOND, 1);

        SemesterDto next = allSemesters
                .stream()
                .filter(s -> ! s.isStartInPast(startLimit))
                .sorted(Comparator.comparing(SemesterDto::getStart))
                .findFirst()
                .get();

        return next;
    }

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SemesterDto that = (SemesterDto) o;

		if (yaer != that.yaer) return false;
		return type == that.type;

	}

	public int hashCode() {
		int result = yaer;
		result = 31 * result + type.hashCode();
		return result;
	}

	/**
	 * sets the label to a new Semester instance. The Id will not be persisted.
	 * @return
	 */
	public Semester toEntity(){
		Semester semesterEntity = new Semester(yaer, type);
		semesterEntity.setId(id);
		return semesterEntity;
	}

    public String toString() {
        return "SemesterDto{" + "id=" + id + ", yaer=" + yaer + ", type=" + type + '}';
    }
}
