package at.ac.tuwien.inso.sqm.dto;

import at.ac.tuwien.inso.sqm.entity.Tag;

public class AddCourseTag {

    private Tag tag;
    private boolean acctive;

    public AddCourseTag (Tag tag, boolean active) {
        this.tag = tag;
        this.acctive = active;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public boolean isActive() {
        return acctive;
    }

    public void setActive(boolean active) {
        this.acctive = active;
    }

    public boolean equals(AddCourseTag o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        if (o != null && acctive != o.acctive) {
            return false;
        }

        boolean isequal = tag != null ? tag.equals(o.tag) : o.tag == null;
        return isequal;
    }

}
