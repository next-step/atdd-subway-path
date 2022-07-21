package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Line addSection(Section section) {
        sections.add(section);
        return this;
    }

    public List<Station> findAllStations() {
        if (sections.isEmpty()) {
            return List.of();
        }
        return Stream.concat(
                        Stream.of(sections.get(0).getUpStation()),
                        sections.stream().map(Section::getDownStation)
                )
                .collect(Collectors.toList());
    }

    public Line removeSection(Station station) {
        validateRemoveSection(station);
        sections.removeIf(section -> section.getDownStation().equals(station));
        return this;
    }

    private void validateRemoveSection(Station station) {

        if (sections.isEmpty()) {
            throw new IllegalStateException("노선에 등록된 구간이 존재하지 않아 삭제할 수 없습니다.");
        }

        if (sections.size() == 1) {
            throw new IllegalArgumentException("구간에 상행 종착역과 하행 종착역만 있기 때문에 삭제할 수 없습니다.");
        }

        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException("하행 종착역만을 삭제할 수 있습니다.");
        }
    }
}
