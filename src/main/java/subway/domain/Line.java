package subway.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.dto.LineRequest;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    private Long distance;

    @Embedded
    private Sections sections;

    @Builder
    public Line(String name, String color, Long distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.sections = new Sections(
            Section.builder()
                .line(this)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build()
        );
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public Line update(LineRequest request) {
        this.name = request.getName().isBlank() ? this.name : request.getName();
        this.color = request.getColor().isBlank() ? this.color : request.getColor();
        return this;
    }

    public void addSection(Section section) {
        this.sections.add(section);
        this.distance = this.sections.getTotalDistance();
    }

    public void removeSection(Station station) {
        this.sections.remove(station);
        this.distance = this.sections.getTotalDistance();
    }
}
