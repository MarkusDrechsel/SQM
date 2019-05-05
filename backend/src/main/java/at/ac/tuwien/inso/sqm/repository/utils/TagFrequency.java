package at.ac.tuwien.inso.sqm.repository.utils;

import at.ac.tuwien.inso.sqm.entity.Tag;

//TODO should that DTO not be placed to module 'domain'?!!?!
public class TagFrequency {

    private Tag tag;

    private long frequency;

    public TagFrequency(Tag tag, long frequency) {
        this.tag = tag;
        this.frequency = frequency;
    }

    public Tag getTag() {
        return tag;
    }

    public long getFrequency() {
        return frequency;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagFrequency that = (TagFrequency) o;

        if (frequency != that.frequency) return false;
        return tag != null ? tag.equals(that.tag) : that.tag == null;

    }

    public int hashCode() {
        int result = tag != null ? tag.hashCode() : 0;
        result = 31 * result + (int) (frequency ^ (frequency >>> 32));
        return result;
    }

    public String toString() {
        return "TagFrequency{" +
                "tag=" + tag +
                ", frequency=" + frequency +
                '}';
    }
}
