package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.domain.VO.SectionsVO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        return sections.getSortedStations();
    }

    public void removeSection(Station station) {
        sections.removeSection(station);
    }

    public void updateNameOrColor(LineRequest request) {
        if (request.getName() != null) {
            this.name = request.getName();
        }
        if (request.getColor() != null) {
            this.color = request.getColor();
        }
    }

    public boolean hasSection() {
        return sections.hasSection();
    }
}
