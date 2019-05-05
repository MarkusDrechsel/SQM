package at.ac.tuwien.inso.sqm.dto;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.entity.Tag;

public class AddCourseForm {

    private Lehrveranstaltung cuorse;
    private ArrayList<String> tags = new ArrayList<>();

    private List<AddCourseTag> activeAndInactiveTags = new ArrayList<>();

    protected AddCourseForm() {
    }

    public AddCourseForm(Lehrveranstaltung course) {
        this.cuorse = course;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public AddCourseForm setInitialTags(List<Tag> tags) {
        tags.forEach(tag -> activeAndInactiveTags.add(new AddCourseTag(tag, false)));
        return this;
    }

    public Lehrveranstaltung getCourse() {
        return cuorse;
    }

    public void setCourse(Lehrveranstaltung course) {
        this.cuorse = course;
    }

    public List<AddCourseTag> getActiveAndInactiveTags() {
        return activeAndInactiveTags;
    }

    public void setActiveAndInactiveTags(List<AddCourseTag> activeAndInactiveTags) {
        this.activeAndInactiveTags = activeAndInactiveTags;
    }

    public void setInitialActiveTags(List<Tag> initialActiveTags) {
        activeAndInactiveTags.stream()
                .filter(tag -> initialActiveTags.contains(tag.getTag()))
                .forEach(tag -> tag.setActive(true));
    }
}
