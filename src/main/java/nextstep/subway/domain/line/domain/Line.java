package nextstep.subway.domain.line.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20)
    private String color;

    @Embedded
    private Sections sections = new Sections();

    @Builder
    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line create(String name, String color) {
        return Line.builder()
                .name(name)
                .color(color)
                .build();
    }

    public void update(String name, String color) {
        if (name != null) {
            this.name = name;
        }

        if (color != null) {
            this.color = color;
        }
    }

    public boolean isStationDirectionEqual(Long stationId) {
        if (sections.isSectionsNullOrEmpty()) {
            return true;
        }

        Section lastSection = sections.getLastSection();
        return lastSection.isDownStation(stationId);
    }

    public boolean containsSectionByStation(Long stationId) {
        if (sections.isSectionsNullOrEmpty()) {
            return true;
        }

        for (Section section : sections.getSections()) {
            Station upStation = section.getUpStation();
            Station downStation = section.getUpStation();

            if (upStation.getId().equals(stationId) || downStation.getId().equals(stationId)) {
                return false;
            }
        }

        return true;
    }

    public boolean hasMoreThanOne(Long stationId) {
        return sections.hasMoreThanOne(stationId);
    }

    public void removeSections(){
        sections.removeSection();
    }

    public void addSection(Station upStation, Station downStation, int distance){
        sections.addSection(upStation, downStation, distance);
    }

    public List<Section> getSections(){
        return sections.getSections();
    }
}
