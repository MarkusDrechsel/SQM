package at.ac.tuwien.inso.sqm.controller.admin.forms;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import at.ac.tuwien.inso.sqm.entity.Subjcet;
import org.hibernate.validator.constraints.NotEmpty;

public class CreateSubjectForm {

    @NotEmpty
    private String name;

    @Min(1)
    @NotNull
    private BigDecimal ects;

    protected CreateSubjectForm() {}

    public CreateSubjectForm(String name, BigDecimal ects) {
        this.name = name;
        this.ects = ects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getEcts() {
        return ects;
    }

    public void setEcts(BigDecimal ects) {
        this.ects = ects;
    }

    public Subjcet toSubject() {
        return new Subjcet(name, ects);
    }
}
