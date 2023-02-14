package nextstep.subway.domain;

import nextstep.subway.exception.IllegalSectionAddException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Line line, Station upStation, Station downStation, int distance) {
        Optional<Section> middleSection = sections.stream()
                .filter(s -> s.getUpStation().equals(upStation))
                .findAny();

        if (middleSection.isPresent()) {
            Section section = middleSection.get();
            validateSectionDistance(distance, section);
            sections.remove(section);
            sections.add(new Section(
                    line,
                    upStation,
                    downStation,
                    distance)
            );
            sections.add(new Section(
                    line,
                    downStation,
                    section.getDownStation(),
                    section.getDistance() - distance)
            );
            return;
        }
        sections.add(new Section(
                line,
                upStation,
                downStation,
                distance)
        );
    }

    private static void validateSectionDistance(int distance, Section section) {
        if (distance > section.getDistance()) {
            throw new IllegalSectionAddException();
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        if (sections.isEmpty()) {
            return stations;
        }
        stations.add(sections.get(0).getUpStation());
        sections.stream()
                .map(section -> section.getDownStation())
                .forEach(downStation -> stations.add(downStation));
        return stations;
    }

    public void removeSection() {
        sections.remove(sections.size()-1);
    }

    public Station getDownStation() {
        return sections.get(sections.size()-1).getDownStation();
    }

    public Section getFirstSection() {
        return sections.get(0);
    }
    public Section getLastSection() {
        return sections.get(sections.size()-1);
    }
}
