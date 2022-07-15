package nextstep.subway.domain;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String color;

	@Embedded
	private Sections sections = new Sections();

	public Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line(Long id, String name, String color) {
		this.id = id;
		this.name = name;
		this.color = color;
	}

	public static Line of(String name, String color) {
		return new Line(name, color);
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public List<Section> getSections() {
		return this.sections.getSections();
	}

	public List<Station> getStations() {
		return this.sections.getStations();
	}

	public void addSection(Station upStation, Station downStation, int distance) {
		this.sections.add(new Section(this, upStation, downStation, distance));
	}

	public void removeSection(Station station) {
		this.sections.remove(station);
	}

	public void changeName(String lineName) {
		if (lineName != null) {
			this.name = lineName;
		}
	}

	public void changeColor(String lineColor) {
		if (lineColor != null) {
			this.color = lineColor;
		}
	}

}
