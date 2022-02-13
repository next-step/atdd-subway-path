package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Line extends BaseEntity {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.stream()
                .sorted(Comparator.comparing(Section::getId))
                .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = Section.builder()
                .line(this)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
        this.sections.add(section);
    }

    public void deleteSection(Station station) {
        validateDeletion(station);
        removeLastSection();
    }

    private void removeLastSection() {
        int lastSectionIdx = this.sections.size() - 1;
        this.sections.remove(lastSectionIdx);
    }

    private void validateDeletion(Station station) {
        if (!isLastSection(station)) {
            throw new IllegalArgumentException("마지막 구간만 삭제할 수 있습니다. stationId를 확인해주세요.");
        }
    }

    private boolean isLastSection(Station station) {
        return getLastSection().getDownStation().isEqualTo(station);
    }

    private Section getLastSection() {
        return getSections().get(sections.size() - 1);
    }

    public void update(Line line) {
        if (isNameValid()) {
            name = line.getName();
        }
        if (isColorValid()) {
            color = line.getColor();
        }
    }

    private boolean isNameValid() {
        return this.name != null;
    }

    private boolean isColorValid() {
        return this.color != null;
    }

    public boolean hasAnySection() {
        return !this.sections.isEmpty();
    }

    public List<Station> getStations() {
        Stream<Station> downStations = this.sections.stream().map(Section::getDownStation);
        Stream<Station> upStations = this.sections.stream().map(Section::getUpStation);

        return allStationsOrderedById(downStations, upStations);
    }

    private List<Station> allStationsOrderedById(Stream<Station> downStations, Stream<Station> upStations) {
        return Stream.concat(downStations, upStations)
                .distinct()
                .sorted(Comparator.comparing(Station::getId))
                .collect(Collectors.toList());
    }
}
