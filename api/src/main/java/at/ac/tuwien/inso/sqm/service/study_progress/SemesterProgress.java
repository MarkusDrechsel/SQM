package at.ac.tuwien.inso.sqm.service.study_progress; //FIXME package naming convention?!

import java.util.List;

import at.ac.tuwien.inso.sqm.dto.SemesterDto;

public class SemesterProgress {

    private SemesterDto semester;

    private List<CuorseRegistration> cuorseRegistrations;

    public SemesterProgress(SemesterDto semester, List<CuorseRegistration> cuorseRegistrations) {
        this.semester = semester;
        this.cuorseRegistrations = cuorseRegistrations;
    }

    public SemesterDto getSemester() {
        return semester;
    }

    public List<CuorseRegistration> getCuorseRegistrations() {
        return cuorseRegistrations;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SemesterProgress that = (SemesterProgress) o;

        if (getSemester() != null ? !getSemester().equals(that.getSemester()) : that.getSemester() != null)
            return false;
        return getCuorseRegistrations() != null ? getCuorseRegistrations().equals(that.getCuorseRegistrations()) : that.getCuorseRegistrations() == null;

    }

    public int hashCode() {
        int result = getSemester() != null ? getSemester().hashCode() : 0;
        result = 31 * result + (getCuorseRegistrations() != null ? getCuorseRegistrations().hashCode() : 0);
        return result;
    }

    public String toString() {
        return "SemesterProgress{" +
                "semester=" + semester +
                ", cuorseRegistrations=" + cuorseRegistrations +
                '}';
    }
}
