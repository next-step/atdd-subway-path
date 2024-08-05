package nextstep.subway.line.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;
import nextstep.subway.line.exception.InvalidDownStationException;
import nextstep.subway.line.exception.InvalidSectionLengthException;
import nextstep.subway.line.exception.InvalidUpStationException;
import nextstep.subway.line.exception.SectionAlreadyExistsException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotFoundException;

@Embeddable
@Getter
@NoArgsConstructor
public class LineSections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
        CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<LineSection> lineSections = new ArrayList<>();

    public void addSection(LineSection newLineSection) {
        if (lineSections.isEmpty()) {
            lineSections.add(newLineSection);
            return;
        }

        validateNewLineSection(newLineSection);
        addSectionToAppropriatePosition(newLineSection);
    }

    public void deleteSection(Station station) {
        validateDeletelineSection();
        deleteSectionByPosition(station);
    }

    private void validateNewLineSection(LineSection newLineSection) {

        Station newUpStation = newLineSection.getUpStation();
        Station newDownStation = newLineSection.getDownStation();

        // 상행역은 기존 노선 존재하지 않고, 하행역이 상행 종착역이 아닌 경우
        if (!isContainStations(newUpStation) && !isSameAsFirstUpStation(newDownStation)) {
            throw new InvalidDownStationException(newDownStation.getId());
        }

        // 새로운 노선이 기존 노선에 이미 존재하는 경우
        if (isSameStation(newUpStation, newDownStation)) {
            throw new SectionAlreadyExistsException(newUpStation.getId(), newDownStation.getId());
        }

        // 상행역이 기존 상행역에 존재하고, 하행역이 기존역에 존재하는 경우
        if (isContainUpStations(newUpStation) && isContainStations(newDownStation)) {
            throw new InvalidDownStationException(newLineSection.getDistance());
        }

        // 상행역이 기존 상행역에 존재하고, 하행역이 기존 하행역보다 길거나 같은 경우
        if (isContainUpStations(newUpStation) && isLongerThanExistingSection(newLineSection)) {
            throw new InvalidSectionLengthException(newLineSection.getDistance());
        }
    }

    private void addSectionToAppropriatePosition(LineSection newLineSection) {

        Station newUpStation = newLineSection.getUpStation();
        Station newDownStation = newLineSection.getDownStation();

        if (!isContainUpStations(newUpStation) && isSameAsFirstUpStation(newDownStation)) {
            lineSections.add(0, newLineSection);
            return;
        }
        if (isSameAsLastDownStation(newUpStation)) {
            lineSections.add(newLineSection);
            return;
        }

        addLineSectionInMiddle(newLineSection);
    }

    private void addLineSectionInMiddle(LineSection newLineSection) {
        LineSection existingSection = findSectionWithUpStation(newLineSection.getUpStation());
        LineSection newDownSection = createNewDownSection(newLineSection, existingSection);
        replaceSectionWithNewSections(newLineSection, existingSection, newDownSection);
    }

    private LineSection findSectionWithUpStation(Station newUpStation) {
        return lineSections.stream()
            .filter(section -> section.hasSameUpStation(newUpStation))
            .findFirst()
            .orElseThrow(() -> new InvalidUpStationException(newUpStation.getId()));
    }

    private LineSection createNewDownSection(LineSection newLineSection,
        LineSection existingSection) {
        return new LineSection(
            existingSection.getLine(),
            newLineSection.getDownStation(),
            existingSection.getDownStation(),
            existingSection.getDistance() - newLineSection.getDistance()
        );
    }

    private void replaceSectionWithNewSections(LineSection newLineSection,
        LineSection existingSection, LineSection newDownSection) {
        int index = lineSections.indexOf(existingSection);
        lineSections.add(index + 1, newLineSection);
        lineSections.add(index + 2, newDownSection);
        lineSections.remove(existingSection);
    }

    private boolean isLongerThanExistingSection(LineSection newSection) {
        return lineSections.stream()
            .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
            .anyMatch(section -> newSection.getDistance() >= section.getDistance());
    }

    private boolean isSameStation(Station upStation, Station downStation) {
        return lineSections.stream()
            .anyMatch(
                section -> section.getUpStation().equals(upStation) && section.getDownStation()
                    .equals(downStation));
    }

    private boolean isSameAsFirstUpStation(Station downStation) {
        Optional<Station> firstUpStation = getFirstUpStation();
        return firstUpStation.isPresent() && downStation.equals(firstUpStation.get());
    }

    private boolean isSameAsLastDownStation(Station upStation) {
        Optional<Station> lastDownStation = getLastDownStation();
        return lastDownStation.isPresent() && upStation.equals(lastDownStation.get());
    }

    private Optional<Station> getFirstUpStation() {
        return lineSections.stream()
            .findFirst()
            .map(LineSection::getUpStation);
    }

    private Optional<Station> getLastDownStation() {
        if (lineSections.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(lineSections.get(lineSections.size() - 1).getDownStation());
    }

    private boolean isContainStations(Station compareStation) {
        return getStations().stream()
            .anyMatch(station -> station.equals(compareStation));
    }

    private boolean isContainUpStations(Station compareStation) {
        return lineSections.stream()
            .map(LineSection::getUpStation)
            .collect(Collectors.toList()).stream()
            .anyMatch(station -> station.equals(compareStation));
    }

    private void validateDeletelineSection() {
        if (lineSections.size() <= 1) {
            throw new SubwayException(SubwayExceptionType.CANNOT_DELETE_SINGLE_SECTION);
        }
    }

    private void deleteSectionByPosition(Station station) {
        if (isSameAsFirstUpStation(station)) {
            lineSections.remove(0);
            return;
        }
        if (isSameAsLastDownStation(station)) {
            lineSections.remove(lineSections.size() - 1);
            return;
        }

        deleteLineSectionInMiddle(station);
    }

    private void deleteLineSectionInMiddle(Station station) {
        LineSection prevLineSection = getPreviousLineSection(station);
        LineSection nextLineSection = getNextLineSection(station);

        prevLineSection.updateDownStationAndDistance(nextLineSection.getDownStation(),
            prevLineSection.getDistance() + nextLineSection.getDistance());
        lineSections.remove(nextLineSection);
    }

    private LineSection getPreviousLineSection(Station station) {
        return lineSections.stream()
            .filter(lineSection -> lineSection.getDownStation().equals(station))
            .findFirst()
            .orElseThrow(() -> new StationNotFoundException(station.getId()));
    }

    private LineSection getNextLineSection(Station station) {
        return lineSections.stream()
            .filter(lineSection -> lineSection.getUpStation().equals(station))
            .findFirst()
            .orElseThrow(() -> new StationNotFoundException(station.getId()));
    }

    public List<Station> getStations() {
        return lineSections.stream()
            .flatMap(lineSection -> Stream.of(
                lineSection.getUpStation(),
                lineSection.getDownStation()))
            .distinct()
            .collect(Collectors.toList());
    }

    public int size() {
        return lineSections.size();
    }

    public Stream<LineSection> stream() {
        return lineSections.stream();
    }
}
