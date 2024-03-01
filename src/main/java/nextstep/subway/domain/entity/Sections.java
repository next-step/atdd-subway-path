package nextstep.subway.domain.entity;

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
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section addSection) {
        if (alreadyExistingStation(addSection)) {
            throw new IllegalArgumentException("invalid section");
        }
        if (isAddingToLastSection(addSection)) {
            sections.add(addSection);
        } else {
            addToExistingSection(addSection);
        }
    }

    public boolean alreadyExistingStation(Section section) {
        return this.getSortedStationsByUpDirection(true).stream()
            .anyMatch(station -> Objects.equals(station, section.getDownStation()));
    }

    public List<Station> getSortedStationsByUpDirection(boolean isUpDirection) {
        List<Station> stations = this.sections.stream()
            .sorted()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .distinct()
            .collect(Collectors.toList());
        if (!isUpDirection) {
            Collections.reverse(stations);
        }
        return stations;

    }

    public void removeStation(Station removeStation) {
        if (getSectionSize() == 1) {
            throw new IllegalStateException("invalid section state");
        }
        Optional<Section> removeSection = getSectionToRemove(removeStation);
        if (removeSection.isEmpty()) {
            throw new IllegalArgumentException("not found remove station: " + removeStation.getId());
        }
        sections.remove(removeSection.get());
    }

    private Optional<Section> getSectionToRemove(Station removeStation) {
        if (isSameStartStationAs(removeStation)) {
            return getSectionByUpStation(removeStation);
        }
        if (isSameEndStationAs(removeStation)) {
            return getSectionByDownStation(removeStation);
        }
        return getMergedSectionForRemove(removeStation);
    }

    private Optional<Section> getMergedSectionForRemove(Station removeStation) {
        Optional<Section> leftSectionByUpDirectionOpt = getSectionByDownStation(removeStation);
        Optional<Section> rightSectionByUpDirectionOpt = getSectionByUpStation(removeStation);
        if (leftSectionByUpDirectionOpt.isEmpty() || rightSectionByUpDirectionOpt.isEmpty()) {
            return Optional.empty();
        }
        Section leftSectionByUpDirection = leftSectionByUpDirectionOpt.get();
        Section rightSectionByUpDirection = rightSectionByUpDirectionOpt.get();
        leftSectionByUpDirection.changeDownStation(rightSectionByUpDirection.getDownStation());
        leftSectionByUpDirection.plusDistance(rightSectionByUpDirection.getDistance());
        return rightSectionByUpDirectionOpt;
    }


    public Optional<Station> getStartStation() {
        return getSortedStationsByUpDirection(true).stream().findFirst();
    }

    public Optional<Station> getEndStation() {
        return getSortedStationsByUpDirection(false).stream().findFirst();
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
        section.changeUpStation(addSection.getDownStation());
        section.minusDistance(addSection.getDistance());
        int idx = sections.indexOf(section);
        sections.add(idx, addSection);
    }

    private boolean isAddingToLastSection(Section addSection) {
        return sections.isEmpty() || isSameEndStationAs(addSection.getUpStation());
    }

    private boolean isSameStartStationAs(Station station) {
        return getStartStation().map(firstStation -> firstStation.equals(station)).orElse(false);
    }

    private boolean isSameEndStationAs(Station station) {
        return getEndStation().map(endStation -> endStation.equals(station)).orElse(false);
    }

    private Optional<Section> getSectionByUpStation(Station station) {
        return this.getSections().stream()
            .filter(section -> Objects.equals(section.getUpStation(), station))
            .findFirst();
    }

    private Optional<Section> getSectionByDownStation(Station station) {
        return this.getSections().stream()
            .filter(section -> Objects.equals(section.getDownStation(), station))
            .findFirst();
    }
}
