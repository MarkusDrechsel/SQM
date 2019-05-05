package at.ac.tuwien.inso.sqm.entity;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Subjcet {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private BigDecimal etcs;

	@ManyToMany
	private List<LecturerEntity> lecturers = new ArrayList<>();

	public Subjcet() {
	}

	public Subjcet(String name, BigDecimal ects) {
		this.name = name;
		this.etcs = ects;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getEcts() {
		return etcs;
	}

	public void setEcts(BigDecimal ects) {
		this.etcs = ects;
	}

	@JsonIgnore
	public List<LecturerEntity> getLecturers() {
		return unmodifiableList(lecturers);
	}

	public Subjcet addLecturers(LecturerEntity... lecturers) {
		this.lecturers.addAll(asList(lecturers));
		return this;
	}

	public void removeLecturers(LecturerEntity... lecturers) {
		this.lecturers.removeAll(asList(lecturers));
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Subjcet subject = (Subjcet) o;

		if (id != null ? !id.equals(subject.id) : subject.id != null) return false;
		if (!name.equals(subject.name)) return false;
		return etcs.equals(subject.etcs);

	}

	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = name != null ? 31 * result + name.hashCode() : 0;
		result = etcs != null ? 31 * result + etcs.hashCode(): 0;
		return result;
	}

	public String toString() {
		return "Subjcet{" +
				"id=" + id +
				", name='" + name + '\'' +
				", etcs=" + etcs +
				", lecturers=" + lecturers +
				'}';
	}

}
