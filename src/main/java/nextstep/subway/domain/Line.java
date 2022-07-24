package nextstep.subway.domain;

import javax.persistence.*;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;

@Getter
@Entity
public class Line {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NonNull
	private String name;
	@NonNull
	private String color;

	@Embedded
	private Sections sections;

	public Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
		this.sections = new Sections();
	}

	public Line(Long id, String name, String color) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.sections = new Sections();
	}

	public void addSection(Section section) {
		this.sections.add(section);
	}

	public List<Station> getStations() {
        return this.sections.getStations();
	}

	public void removeSection(Station station) {
		this.sections.remove(station);
	}

	public void update(String name, String color) {
		if (name != null) {
			this.name = name;
		}
		if (color != null) {
			this.color = color;
		}
	}

	public Boolean isSectionEmpty() {
		return this.sections.isEmpty();
	}
}
