package subway.line;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import subway.station.Station;

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
	private Sections sections = new Sections();

	protected Line() {
	}

	public Line(String name, String color) {
		validateName(name);
		validateColor(color);

		this.name = name;
		this.color = color;
	}

	private void validateName(String name) {
		if (name == null || name.isBlank() || name.length() > 20) {
			throw new IllegalArgumentException("노선의 이름은 공백이 아니거나 20자 이하여야 합니다.");
		}
	}

	private void validateColor(String color) {
		if (color == null || color.isBlank() || color.length() > 20) {
			throw new IllegalArgumentException("노선의 색깔은 공백이 아니거나 20자 이하여야 합니다.");
		}
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

	public Sections getSections() {
		return sections;
	}

	public void changeName(String name) {
		validateName(name);
		this.name = name;
	}

	public void changeColor(String color) {
		validateColor(color);
		this.color = color;
	}

	public void addSection(Section section) {
		getSections().add(section);
	}

	public void removeFinalStation(Station finalStation) {
		getSections().remove(finalStation);
	}

	public List<Station> getSortedStations() {
		return getSections().getSortedStations();
	}

	public List<Section> getSortedSections() {
		return getSections().getSortedSections();
	}
}
