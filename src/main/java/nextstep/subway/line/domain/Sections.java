package nextstep.subway.line.domain;

import nextstep.subway.line.exception.CannotAddSectionException;
import nextstep.subway.line.exception.CannotRemoveSectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationAlreadyExistException;
import nextstep.subway.station.exception.StationNonExistException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.line.exception.LineExceptionMessage.EXCEPTION_MESSAGE_INVALID_SECTION_DISTANCE;
import static nextstep.subway.line.exception.LineExceptionMessage.EXCEPTION_MESSAGE_NOT_DELETABLE_SECTION;
import static nextstep.subway.station.exception.StationExceptionMessage.*;

@Embeddable
public class Sections {

    private static final int NUMBER_ONE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        boolean existUpStation = isExistStation(section.getUpStation());
        boolean existDownStation = isExistStation(section.getDownStation());

        validateAddableSection(existUpStation, existDownStation);

        if (existUpStation) {
            addForwardSection(section);
        }
        if (existDownStation) {
            addBackwardSection(section);
        }
    }

    private boolean isExistStation(Station station) {
        return sections.stream()
                .anyMatch(section -> isEqualsStation(section, station));
    }

    private boolean isEqualsStation(Section section, Station station) {
        return Objects.equals(section.getUpStation(), station) || Objects.equals(section.getDownStation(), station);
    }

    private void validateAddableSection(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new StationAlreadyExistException(EXCEPTION_MESSAGE_EXIST_STATION_IN_SECTION);
        }
        if (!existUpStation && !existDownStation) {
            throw new StationNonExistException(EXCEPTION_MESSAGE_NON_EXIST_STATION_IN_SECTION);
        }
    }

    private void addForwardSection(Section section) {
        Section findSection = findSectionByUpStation(section.getUpStation())
                .orElseGet(() -> null);

        if (Objects.isNull(findSection)) {
            sections.add(section);
            return;
        }

        int findSectionDistance = findSection.getDistance();
        int newSectionDistance = section.getDistance();

        validateSectionDistance(findSectionDistance, newSectionDistance);

        int findSectionIndex = sections.indexOf(findSection);
        int adjustedDistance = calculateAdjustedDistance(findSectionDistance, newSectionDistance);

        sections.set(findSectionIndex, section);
        sections.add(findSectionIndex + 1,
                new Section(section.getLine(), section.getDownStation(), findSection.getDownStation(), adjustedDistance));
    }

    private void addBackwardSection(Section section) {
        Section findSection = findSectionByDownStation(section.getDownStation())
                .orElseGet(() -> null);

        if (Objects.isNull(findSection)) {
            sections.add(section);
            return;
        }

        int findSectionDistance = findSection.getDistance();
        int newSectionDistance = section.getDistance();

        validateSectionDistance(findSectionDistance, newSectionDistance);

        int findSectionIndex = sections.indexOf(findSection);
        int adjustedDistance = calculateAdjustedDistance(findSectionDistance, newSectionDistance);

        sections.set(findSectionIndex, section);
        sections.add(findSectionIndex,
                new Section(section.getLine(), findSection.getUpStation(), section.getUpStation(), adjustedDistance));
    }

    private Optional<Section> findSectionBy(Predicate<Section> predicate) {
        return sections.stream()
                .filter(predicate)
                .findFirst();
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return findSectionBy(section -> section.getUpStation().equals(upStation));
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return findSectionBy(section -> section.getDownStation().equals(downStation));
    }

    private void validateSectionDistance(int findSectionDistance, int newSectionDistance) {
        if (findSectionDistance <= newSectionDistance) {
            throw new CannotAddSectionException(EXCEPTION_MESSAGE_INVALID_SECTION_DISTANCE);
        }
    }

    private int calculateAdjustedDistance(int findSectionDistance, int newSectionDistance) {
        return findSectionDistance - newSectionDistance;
    }
    // TODO : 구간 삭제 기능 변경
    public void deleteLastDownStation() {
        validateDeletableCurrentSections();

        sections.remove(getLastSection());
    }

    private Station getLastDownStation() {
        Section section = getLastSection();
        return section.getDownStation();
    }

    private Section getLastSection() {
        return sections.get(sections.size() - NUMBER_ONE);
    }

    private void validateDeletableCurrentSections() {
        if (sections.size() == NUMBER_ONE || sections.isEmpty()) {
            throw new CannotRemoveSectionException(EXCEPTION_MESSAGE_NOT_DELETABLE_SECTION);
        }
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStations() {
        return sections.stream()
                .sorted()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
}
