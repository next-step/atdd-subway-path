package nextstep.subway.line;

import java.util.List;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	private String name;

	private String color;

	@Embedded
	private Sections sections;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
		this.sections = new Sections();
    }

	public void updateLine(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Section addSection(Section section) {
		return sections.addSection(section);
	}

	public void removeSection(Station station) {
		sections.removeSection(station);
	}

	public List<Section> getSections() {
		return sections.getSections();
	}

	public List<Station> getAllStation() {
		return sections.getAllStation();
	}

	public List<Station> getOrderStation() {
		return sections.getOrderStation();
	}

	public int getDistance() {
		return sections.getDistance();
	}
}
