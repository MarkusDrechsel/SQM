package at.ac.tuwien.inso.sqm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tag {

    @Id
    @GeneratedValue
    private Long Id;

    @Column(unique = true)
    private String name;

    @Column
    private boolean valid = true;

    protected Tag() {}

    public Tag(String name) {
        this.name = name;
    }

    public Tag(String name, boolean valid) {
        this.name = name;
        this.valid = valid;
    }

    public Long getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return name.equals(tag.name);

    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return "Tag{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                '}';
    }
}
