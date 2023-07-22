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
    private Sections sections = new Sections();

    @Builder
    public Line(String name, String color, Long distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.sections = new Sections(List.of(
            Section.builder()
                .line(this)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build()
        ));
    }

    public void update(LineRequest request) {
        this.name = request.getName().isBlank() ? this.name : request.getName();
        this.color = request.getColor().isBlank() ? this.color : request.getColor();
    }

    public void addSection(Section section) {
        this.sections.add(section);
        this.distance += section.getDistance();
    }

    public void removeSection() {
        this.distance -= sections.getLastSection().getDistance();
        this.sections.remove();
    }
}
