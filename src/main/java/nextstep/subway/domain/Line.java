package nextstep.subway.domain;

import java.util.stream.Collectors;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    @Builder
    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(String name, String color) {
        if(name != null) this.name = name;
        if(color != null) this.color = color;
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public boolean isEmptySections() {
        return sections.getSections().isEmpty();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public List<Section> getSectionList() {
        return sections.getSections();
    }

    public void removeStation(Station station) {
        sections.removeStation(station);
    }

}
