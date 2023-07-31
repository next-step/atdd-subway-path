package subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.NoArgsConstructor;
import subway.exception.impl.CannotCreateSectionException;
import subway.exception.impl.CannotDeleteSectionException;
import subway.exception.impl.StationNotFoundException;

@Embeddable
@NoArgsConstructor
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private List<Section> sections;

    public Sections(Section section) {
        sections = new ArrayList<>();
        sections.add(section);
    }

    public void add(Section section) {
        processIfInsertedBetween(section);
        sections.add(section);
    }

    private void processIfInsertedBetween(Section newSection) {
        Section connectedSection = getConnectedSection(newSection);

        if (newSection.isInsertedBetween(connectedSection)) {
            if (newSection.hasLoggerDistance(connectedSection)) {
                throw new CannotCreateSectionException();
            }

            Section dividedSection = getDividedSection(connectedSection, newSection);

            sections.add(dividedSection);
            sections.remove(connectedSection);
        }
    }

    private Section getConnectedSection(Section newSection) {
        Set<Station> stationSet = new HashSet<>(getStations());

        if (newSection.isIncludeStations(stationSet) ||
            newSection.isExcludeStations(stationSet)) {
            throw new CannotCreateSectionException();
        }

        Optional<Section> optionalSection = sections.stream()
            .filter(section -> newSection.isInsertedBetween(section) || newSection.isAppendedToEnds(
                section))
            .findFirst();

        if (optionalSection.isEmpty()) {
            throw new CannotCreateSectionException();
        }

        return optionalSection.get();
    }

    private Section getDividedSection(Section connectedSection, Section newSection) {
        Line line = newSection.getLine();
        Long distance = connectedSection.getDistance() - newSection.getDistance();

        return newSection.isSameUpStation(connectedSection) ?
            Section.builder()
                .line(line)
                .upStation(newSection.getDownStation())
                .downStation(connectedSection.getDownStation())
                .distance(distance)
                .build()
            : Section.builder()
                .line(line)
                .upStation(connectedSection.getUpStation())
                .downStation(newSection.getUpStation())
                .distance(distance)
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

    private Optional<Section> findSectionByUpStation(Station station) {
        return this.sections.stream()
            .filter(it -> it.isSameUpStation(station))
            .findFirst();
    }

    public Long getTotalDistance() {
        return sections.stream().mapToLong(Section::getDistance).sum();
    }

    public void remove(Station station) {
        if (hasSingleSection()) {
            throw new CannotDeleteSectionException();
        }

        List<Section> sectionsContainStation = getStationSections(station);

        // 노선에 등록되어있지 않은 역을 제거하는 경우
        if (sectionsContainStation.isEmpty()) {
            throw new CannotDeleteSectionException();
        }

        Optional<Section> firstSection = sectionsContainStation.stream()
            .filter(section -> section.isSameDownStation(station))
            .findAny();
        Optional<Section> secondSection = sectionsContainStation.stream()
            .filter(section -> section.isSameUpStation(station))
            .findAny();

        addSectionForDelete(firstSection, secondSection);

        firstSection.ifPresent(section -> sections.remove(section));
        secondSection.ifPresent(section -> sections.remove(section));
    }

    private List<Section> getStationSections(Station station) {
        return sections.stream()
            .filter(section -> section.containsStation(station))
            .collect(Collectors.toList());
    }

    private void addSectionForDelete(Optional<Section> firstSectionOptional,
        Optional<Section> secondSectionOptional) {
        // 두 구간에 포함되어있는 지하철역을 삭제하는 경우
        if (firstSectionOptional.isPresent() && secondSectionOptional.isPresent()) {
            Section firstSection = firstSectionOptional.get();
            Section secondSection = secondSectionOptional.get();

            Section section = Section.builder()
                .line(firstSection.getLine())
                .upStation(firstSection.getUpStation())
                .downStation(secondSection.getDownStation())
                .distance(firstSection.getDistance() + secondSection.getDistance())
                .build();
            sections.add(section);
        }
    }

    public int getSize() {
        return sections.size();
    }

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

}
