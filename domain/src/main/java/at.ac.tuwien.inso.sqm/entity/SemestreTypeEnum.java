package at.ac.tuwien.inso.sqm.entity;


import java.util.Calendar;

public enum SemestreTypeEnum {
    //TODO not sure if this is right, should semester start be hardcoded here?
    WinterSemester("WS", Calendar.OCTOBER, 1),
    SummerSemester("SS", Calendar.MARCH, 1);

    /**
     * Name of the semester: WS or SS
     */
    private String name;

    /**
     * Month the semester starts
     */
    private int statrMonth;

    /**
     * Day in month the semester starts
     */
    private int startDay;


    SemestreTypeEnum(String name, int startMonth, int startDay) {
        this.name = name;
        this.statrMonth = startMonth;
        this.startDay = startDay;
    }

    public int getStartMonth() {
        return statrMonth;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartMonth(int startMonth) {
        this.statrMonth = startMonth;
    }

    public String toString() {
        return name;
    }

    /**
     * Reverse of toString
     */
    public static SemestreTypeEnum fromString(String name) {
        for (SemestreTypeEnum type : SemestreTypeEnum.values()) {
            if (type.toString().equals(name)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Type '" + name + "' is not a valid SemestreTypeEnum");
    }
}
