package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Line(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Line line = (Line) o;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, sections);
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

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(final List<Section> sections) {
        this.sections = sections;
    }

    public void update(final String name, final String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }

    public void addSection(final Station upStation, final Station downStation, final int distance) {
        if (this.getStations().isEmpty()) {
            this.getSections().add(new Section(this, upStation, downStation, distance));
            return;
        }
        if (this.getStations().containsAll(List.of(upStation, downStation)) ||
                (!this.getStations().contains(upStation) && !this.getStations().contains(downStation))) {
            throw new IllegalArgumentException();
        }

        final Station lastUpStation = this.getSections().get(0).getUpStation();
        final Station lastDownStation = this.getSections().get(this.getSections().size() - 1).getDownStation();
        if (lastDownStation.equals(upStation)) {
            this.getSections().add(new Section(this, upStation, downStation, distance));
            return;
        }
        if (lastUpStation.equals(downStation)) {
            this.getSections().add(0, new Section(this, upStation, downStation, distance));
            return;
        }
        sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .ifPresent(it -> {
                    if (distance >= it.getDistance()) {
                        throw new IllegalArgumentException();
                    }
                    sections.add(new Section(this, upStation, downStation, distance));
                    sections.add(new Section(this, downStation, it.getDownStation(), it.getDistance() - distance));
                    sections.remove(it);
                });
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return List.of();
        }

        final List<Station> stations = new ArrayList<>();
        final Section lastUpSection = getLastUpSection(sections.stream().findFirst().get());
        stations.add(lastUpSection.getUpStation());
        stations.add(lastUpSection.getDownStation());

        Section currentSection = lastUpSection;
        while (true) {
            final Section nextSection = getNextSection(currentSection);
            if (nextSection == null) break;
            stations.add(nextSection.getDownStation());
            currentSection = nextSection;
        }
        return stations;
    }

    public void removeLastSection(final Station station) {
        if (!this.getSections().get(this.getSections().size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        this.getSections().remove(this.getSections().size() - 1);
    }

    private Section getLastUpSection(final Section section) {
        final Optional<Section> findSection = this.sections.stream()
                .filter(it -> it.getDownStation().equals(section.getUpStation()))
                .findFirst();
        if (findSection.isEmpty()) {
            return section;
        }
        return getLastUpSection(findSection.get());
    }

    private Section getNextSection(final Section section) {
        final Optional<Section> findSection = this.sections.stream()
                .filter(it -> it.getUpStation().equals(section.getDownStation()))
                .findFirst();
        if (findSection.isEmpty()) {
            return null;
        }
        return findSection.get();
    }
}
