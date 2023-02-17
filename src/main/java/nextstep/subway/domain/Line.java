package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import nextstep.subway.applicaion.dto.LineRequest;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
        CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

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

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void deleteSection(Station station) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int lastSectionIndex = sections.size() - 1;
        Station lastSectionDownStation = sections.get(lastSectionIndex).getDownStation();

        if (!lastSectionDownStation.equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(lastSectionIndex);
    }

    public List<Station> getStations() {
        return sections.stream()
            .flatMap(section ->
                Stream.of(section.getUpStation(), section.getDownStation())
            )
            .distinct()
            .collect(Collectors.toList());
    }

    public void update(LineRequest lineRequest) {
        if (lineRequest.getName() != null) {
            this.name = lineRequest.getName();
        }
        if (lineRequest.getColor() != null) {
            this.color = lineRequest.getColor();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Line)) {
            return false;
        }

        Line line = (Line) obj;
        return name.equals(line.getName())
            && color.equals(line.getColor());
    }
}
