package nextstep.subway.domain;

import java.util.List;

import javax.persistence.Column;
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

	@Column(length = 20, nullable = false)
	private String name;

	@Column(length = 20, nullable = false)
	private String color;

	@Embedded
	private Sections sections;

	public Line() {
		this.sections = new Sections();
	}

	public Line(Long id, String name, String color) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.sections = new Sections();
	}

	public Line(String name, String color) {
		this(null, name, color);
	}

	public void addSection(Station upStation, Station downStation, int distance) {
		this.sections.addSection(new Section(this, upStation, downStation, distance));
	}

	public void removeSection(Long stationId) {
		this.sections.removeSection(stationId);
	}

	public void update(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public List<Station> getStations() {
		return sections.getStations();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public List<Section> getSections() {
		return sections.getSections();
	}
}
