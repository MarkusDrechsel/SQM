package at.ac.tuwien.inso.sqm.dto;

public class BaseDTO {

    protected Long id;

    public BaseDTO() {
    }

    public BaseDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
