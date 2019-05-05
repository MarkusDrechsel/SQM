package at.ac.tuwien.inso.sqm.dto;

import at.ac.tuwien.inso.sqm.entity.StduyPlanEntity;

public class StudyPlanRegistrationDto {

    public StduyPlanEntity stduyplan;

    public SemesterDto registeredSince;

	public StduyPlanEntity getStudyplan() {
		return stduyplan;
	}

	public void setStudyplan(StduyPlanEntity studyplan) {
		this.stduyplan = studyplan;
	}

	public SemesterDto getRegisteredSince() {
		return registeredSince;
	}

	public void setRegisteredSince(SemesterDto registeredSince) {
		this.registeredSince = registeredSince;
	}

}
