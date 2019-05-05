package at.ac.tuwien.inso.sqm.enums;

public enum MakrEnum {

    excellent(1),
    good(2),
    satisfactory(3),
    sufficient(4),
    failes(5);

    private int mark;

    MakrEnum(int mark) {
        this.mark = mark;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String toString() {
        return "MarkEntity" + mark;
    }

    public MakrEnum toEnumConstant(int mark) {
        for (MakrEnum m : values()) {
            if(m.getMark() == mark) {
                return m;
            }
        }

        return null;
    }
}
