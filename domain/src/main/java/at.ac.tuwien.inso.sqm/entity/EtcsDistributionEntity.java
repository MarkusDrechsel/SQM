package at.ac.tuwien.inso.sqm.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EtcsDistributionEntity {

    @Column(nullable = false)
    private BigDecimal mandatory;

    @Column(nullable = false)
    private BigDecimal optionnal;

    @Column(nullable = false)
    private BigDecimal freeChoice;

    protected EtcsDistributionEntity() {
    }

    public EtcsDistributionEntity(BigDecimal mandatory, BigDecimal optional, BigDecimal freeChoice) {
        this.mandatory = mandatory;
        this.optionnal = optional;
        this.freeChoice = freeChoice;
    }

    public BigDecimal getMandatory() {
        return mandatory;
    }

    public BigDecimal getOptional() {
        return optionnal;
    }

    public BigDecimal getFreeChoice() {
        return freeChoice;
    }

    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EtcsDistributionEntity that = (EtcsDistributionEntity) o;

        if (mandatory != null ? !mandatory.equals(that.mandatory) : that.mandatory != null) return false;
        if (optionnal != null ? !optionnal.equals(that.optionnal) : that.optionnal != null) return false;
        return freeChoice != null ? freeChoice.equals(that.freeChoice) : that.freeChoice == null;

    }

    public int hashCode() {
        int result = mandatory != null ? mandatory.hashCode() : 0;
        result = 31 * result + (optionnal != null ? optionnal.hashCode() : 0);
        result = 31 * result + (freeChoice != null ? freeChoice.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "EtcsDistributionEntity{" +
                "mandatory=" + mandatory +
                ", optionnal=" + optionnal +
                ", freeChoice=" + freeChoice +
                '}';
    }
}
