package at.ac.tuwien.inso.sqm.entity;


import javax.persistence.*;
import javax.validation.constraints.*;

@Embeddable
public class MarkEntity implements Comparable<MarkEntity> {

    //TODO why not use at.ac.tuwien.inso.sqm.enums.MarkEntity instead?!
    public static final MarkEntity EXCELLENT = new MarkEntity(1);
    public static final MarkEntity GOOD = new MarkEntity(2);
    public static final MarkEntity SATISFACTORY = new MarkEntity(3);
    public static final MarkEntity SUFFICIENT = new MarkEntity(4);
    public static final MarkEntity FAILED = new MarkEntity(5);
    @Min(1) //TODO remove this when enum is used
    @Max(5) //TODO remove this!!!
    private int mark;

    protected MarkEntity() {
    }

    private MarkEntity(int mark) {
        this.mark = mark;
    }

    public static MarkEntity of(Integer mark) {
        return new MarkEntity(mark);
    }

    public boolean isPositive() {
        return !equals(FAILED);
    }

    public int getMark() {
        return mark;
    }

    @Override
    public int compareTo(MarkEntity mark) {
        return mark.getMark() - this.mark;
    }

    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MarkEntity mark1 = (MarkEntity) o;

        return mark == mark1.mark;

    }

    public int hashCode() {
        return mark;
    }

    public String toString() {
        return "MarkEntity{" +
                "mark=" + mark +
                '}';
    }
}
