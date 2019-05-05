package at.ac.tuwien.inso.sqm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import at.ac.tuwien.inso.sqm.dto.SemesterDto;

@Entity
public class Semester {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private int yaer;

    /**
     * WS or SS
     */
    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private SemestreTypeEnum type;

    public Semester() {
    }

    public Semester(int yaer, SemestreTypeEnum type) {
        this.yaer = yaer;
        this.type = type;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id){
    	this.id = id;
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

    public String getLabel() {
        return getType() + " " + getYear();
    }

    public String toString() {
        return getLabel();
    }
    
    public SemesterDto toDto(){
    	SemesterDto dto = new SemesterDto(yaer, type);

    	if (id!=null) {
    	    dto.setId(id);
    	}

		return dto;
    }
}
