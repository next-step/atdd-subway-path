package subway.line.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.station.model.Station;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Line {

    private static final long MINIMAL_SECTION_SIZE = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Builder.Default
    @Embedded
    private LineSections lineSections = new LineSections();

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section newSection) {
        this.lineSections.add(newSection, this);
    }

    public List<Station> getStations() {
        return lineSections.getStations();
    }

    public void deleteSectionByStation(Station station) {
        lineSections.removeSectionByStation(station);
    }
}
