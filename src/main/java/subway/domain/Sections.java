package subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.NoArgsConstructor;
import subway.exception.impl.CannotCreateSectionException;
import subway.exception.impl.StationNotFoundException;

@Embeddable
@NoArgsConstructor
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST }, orphanRemoval = true)
    private List<Section> sections;

    public Sections(Section section) {
        sections = new ArrayList<>();
        sections.add(section);
    }

    public void add(Section section) {
        Section connectedSection = getConnectedSection(section);
        Section dividedSection = getDividedSection(connectedSection, section);

        if (section.isInsertedBetween(connectedSection)) {
            if (section.hasLoggerDistance(connectedSection)) {
                throw new CannotCreateSectionException();
            }

            sections.add(dividedSection);
            sections.remove(connectedSection);
        }

        sections.add(section);
    }

    public Section getConnectedSection(Section newSection) {
        Set<Long> stationIdSet = getStations().stream()
            .map(Station::getId)
            .collect(Collectors.toSet());

        if (newSection.isIncludeStations(stationIdSet) ||
            newSection.isExcludeStations(stationIdSet)) {
            throw new CannotCreateSectionException();
        }

        Optional<Section> optionalSection = sections.stream()
            .filter(section -> newSection.isInsertedBetween(section) || newSection.isAppendedToEnds(section))
            .findFirst();

        if (optionalSection.isEmpty()) {
            throw new CannotCreateSectionException();
        }

        return optionalSection.get();
    }

    public Section getDividedSection(Section connectedSection, Section newSection) {
        Line line = newSection.getLine();

        // 앞에 추가되는 경우
        if (newSection.isInsertedBetween(connectedSection)) {
            return Section.builder()
                .line(line)
                .upStation(newSection.getDownStation())
                .downStation(connectedSection.getDownStation())
                .distance(connectedSection.getDistance() - newSection.getDistance())
                .build();
        }

        return Section.builder()
            .line(line)
            .upStation(connectedSection.getUpStation())
            .downStation(newSection.getUpStation())
            .distance(newSection.getDistance())
            .build();
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        Station upStation = findFirstUpStation();
        List<Station> result = new ArrayList<>();
        result.add(upStation);

        while (true) {
            Station finalUpStation = upStation;
            Optional<Section> section = findSectionByUpStation(finalUpStation);

            if (section.isEmpty()) {
                break;
            }

            upStation = section.get().getDownStation();
            result.add(upStation);
        }

        return result;
    }

    private Station findFirstUpStation() {
        List<Station> upStations = this.sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        List<Station> downStations = this.sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        return upStations.stream()
            .filter(it -> !downStations.contains(it))
            .findFirst()
            .orElseThrow(StationNotFoundException::new);
    }

    private Optional<Section> findSectionByUpStation(Station finalUpStation) {
        return this.sections.stream()
            .filter(it -> it.isSameUpStation(finalUpStation))
            .findFirst();
    }

    public Long getTotalDistance() {
        return sections.stream().mapToLong(Section::getDistance).sum();
    }

    public void remove() {
        sections.remove(getLastIndex());
    }

    public int getSize() { return sections.size(); }

    public Section getLastSection() {
        return sections.get(getLastIndex());
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    public Station getLastStation() {
        return sections.get(getLastIndex()).getDownStation();
    }

    public boolean hasSingleSection() {
        return sections.size() == 1;
    }

    public boolean isNotLastStation(Long stationId) {
        return !getLastStationId().equals(stationId);
    }

    private Long getLastStationId() {
        return sections.get(getLastIndex()).getDownStation().getId();
    }

}
