package nextstep.subway.domain;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.exception.CannotRemoveSectionException;
import nextstep.subway.exception.ErrorMessage;

@Entity
public class Line {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String color;

	@Embedded
	private Sections sections = new Sections();

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line(String name, String color, Station upStation, Station downStation, int distance) {
		this(name, color);
		this.addSection(upStation, downStation, distance);
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

	public Sections getSections() {
		return sections;
	}

	public void addSection(Station upStation, Station downStation, int distance) {
		this.getSections().addSection(new Section(this, upStation, downStation, distance));
	}

	public List<Station> getStations() {
		return this.sections.getStations();
	}

	public void removeSection(Station station) {
		if (!sections.isLastDownStation(station)) {
			throw new CannotRemoveSectionException(ErrorMessage.CANNOT_REMOVE_NO_LAST_DOWN_STATION);
		}

		sections.removeLastSection();
	}

	public void updateLine(String name, String color) {
		if (name != null) {
			this.setName(name);
		}
		if (color != null) {
			this.setColor(color);
		}
	}
}
