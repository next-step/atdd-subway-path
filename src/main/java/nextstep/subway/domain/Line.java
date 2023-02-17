package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.dto.LineRequest;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(this, upStation, downStation, distance);
        sections.addSection(section);
    }

    public List<Station> getStations() {
        if (sections.getSections().isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = new ArrayList<>();
        stations.add(sections.getUpStation(0));
        stations.addAll(sections.getDownStations());
        return stations;
    }

    public void removeSection(Station station) {
        sections.removeStationCheck(station);
        sections.removeSection();
    }

    public void updateNameOrColor(LineRequest request) {
        if (request.getName() != null) {
            this.name = request.getName();
        }
        if (request.getColor() != null) {
            this.color = request.getColor();
        }
    }
}
