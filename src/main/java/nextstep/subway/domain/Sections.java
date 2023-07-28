package nextstep.subway.domain;

import lombok.NoArgsConstructor;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderColumn(name = "position")
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addMiddle(Line line, Station upStation, Station downStation, int distance) {
        Optional<Section> existSection = sections.stream()
                .filter(section -> section.equalUpStation(upStation) || section.equalDownStation(downStation))
                .findAny();
        if (existSection.isPresent() && existSection.get().getDistance() <= distance) {
            throw new SectionAddException(ErrorType.SECTION_DISTANCE_TOO_LONG);
        }

        int index = sections.indexOf(existSection.get());
        if (existSection.get().equalUpStation(upStation)) {
            sections.get(index).updateDownStationAndDistance(downStation, distance);
            sections.add(index, new Section(line, upStation, downStation, distance));
        }
        if (existSection.get().equalDownStation(downStation)) {
            sections.get(index).updateUpStationAndDistance(upStation, distance);
            sections.add(index + 1, new Section(line, upStation, downStation, distance));
        }
    }

    public void addFirst(Line line, Station upStation, Station downStation, int distance) {
        sections.add(0, new Section(line, upStation, downStation, distance));
    }

    public void addLast(Line line, Station upStation, Station downStation, int distance) {
        sections.add(new Section(line, upStation, downStation, distance));
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, sections.get(0).getUpStation());

        return stations;
    }

    public void removeLast(Station station) {
        Section lastSection = sections.get(sections.size() - 1);
        if (!lastSection.equalDownStation(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(lastSection);
    }
}
