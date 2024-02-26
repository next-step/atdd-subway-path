package nextstep.subway.domians.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section addSection) {
        if (alreadyExistingStation(addSection)) {
            throw new IllegalArgumentException("invalid section");
        }
        if (addToLastSection(addSection)) {
            sections.add(addSection);
        } else {
            addToExistingSection(addSection);
        }
    }

    public boolean alreadyExistingStation(Section section) {
        return this.getStationsBySortedSection(false).stream()
            .anyMatch(station -> Objects.equals(station, section.getDownStation()));
    }

    public List<Station> getStationsBySortedSection(boolean isReverse) {
        var stations = this.sections.stream()
            .sorted()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .distinct()
            .collect(Collectors.toList());
        if (isReverse) {
            Collections.reverse(stations);
        }
        return stations;

    }

    public void removeSection(Long downStationId) {
        if (isNotLastStation(downStationId) || getSectionSize() < 2) {
            throw new IllegalArgumentException("invalid section");
        }
        sections.remove(sections.size() - 1);
    }

    public Optional<Station> getEndStation() {
        return getStationsBySortedSection(true).stream().findFirst();
    }


    public int getSectionSize() {
        return this.sections.size();
    }

    private void addToExistingSection(Section addSection) {
        Optional<Section> existingSection = getSectionByUpStation(addSection.getUpStation());
        if (existingSection.isEmpty()) {
            throw new IllegalArgumentException("Invalid section");
        }
        Section section = existingSection.get();
        int idx = sections.indexOf(section);
        section.changeUpStation(addSection.getDownStation());
        section.minusDistance(addSection.getDistance());
        sections.add(idx, addSection);
    }

    private boolean addToLastSection(Section addSection) {
        Optional<Station> endStation = getEndStation();
        boolean isEqualToAddSectionUpStation = endStation.isPresent()
            && endStation.get().equals(addSection.getUpStation());
        return sections.isEmpty() || isEqualToAddSectionUpStation;
    }


    private Optional<Section> getSectionByUpStation(Station upStation) {
        return this.getSections().stream()
            .filter(section -> Objects.equals(section.getUpStation(), upStation))
            .findFirst();
    }


    private boolean isNotLastStation(Long stationId) {
        Optional<Station> optionalStation = getEndStation();
        if (optionalStation.isPresent()) {
            Station endStation = optionalStation.get();
            return !Objects.equals(endStation.getId(), stationId);
        } else {
            return false;
        }
    }
}
