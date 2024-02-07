package nextstep.subway.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Column(nullable = false)
    private int distance;

    @JsonIgnore
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        this.sections.add(section);
        this.distance += section.getDistance();
    }

    public void deleteSection(Section section) {
        this.sections.remove(section);
        this.distance -= section.getDistance();
    }

    public List<Station> getStations() {
        List<Station> stations = this.sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        stations.add(sections.get(sections.size() - 1).getDownStation()); //종착역 추가
        return stations;
    }

    public void updateNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
