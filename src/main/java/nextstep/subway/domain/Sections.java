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

    public void addFirst(Section section) {
        sections.add(0, section);
    }

    public void addLast(Section section) {
        sections.add(section);
    }

    public void addMiddle(Section newSection) {
        Optional<Section> existSection = sections.stream()
                .filter(section -> section.equalUpStation(newSection.getUpStation()) || section.equalDownStation(newSection.getDownStation()))
                .findAny();
        if (existSection.isPresent() && existSection.get().getDistance() <= newSection.getDistance()) {
            throw new SectionAddException(ErrorType.SECTION_DISTANCE_TOO_LONG);
        }

        int index = sections.indexOf(existSection.get());
        if (existSection.get().equalUpStation(newSection.getUpStation())) {
            sections.get(index).updateDownStationAndDistance(newSection.getDownStation(), newSection.getDistance());
            sections.add(index, new Section(newSection.getLine(), newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance()));
        }
        if (existSection.get().equalDownStation(newSection.getDownStation())) {
            sections.get(index).updateUpStationAndDistance(newSection.getUpStation(), newSection.getDistance());
            sections.add(index + 1, new Section(newSection.getLine(), newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance()));
        }
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
