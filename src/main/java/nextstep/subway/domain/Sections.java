package nextstep.subway.domain;

import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.ValidationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.error.ErrorCode.*;

@Embeddable
public class Sections {

    private static final int FIRST = 0;
    private static final int SECTION_MINIMUM_SIZE = 1;
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addFirst(Section section) {
        if (!sections.isEmpty()) {
            throw new ValidationException(FIRST_SECTION_CREATE_ERROR);
        }
        sections.add(section);
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        save(section);
    }

    public void remove(final Long downStationId) {
        validateDelete(downStationId);
        sections.remove(getLastSection());
    }

    public Section findSectionHasUpStationEndPoint() {
        return sections.stream()
                .filter(s -> s.getUpStation().equals(getUpStationEndPoint()))
                .collect(Collectors.toList())
                .get(FIRST);
    }

    public Section findAnotherSectionWhereDownStationOfTheSectionIsTheUpStation(final Section section) {
        return sections.stream()
                .filter(s -> s.getUpStation().equals(section.getDownStation()))
                .collect(Collectors.toList())
                .get(FIRST);
    }

    private Station getUpStationEndPoint() {
        List<Station> downStations = findDownStations();
        return findUpStations().stream()
                .filter(us -> !downStations.contains(us))
                .findAny()
                .orElseThrow(NotFoundStationException::new);
    }

    public Station getDownStationEndPoint() {
        List<Station> upStations = findUpStations();
        return findDownStations().stream()
                .filter(ds -> !upStations.contains(ds))
                .findAny()
                .orElseThrow(NotFoundStationException::new);
    }

    private void save(final Section target) {
        validateSave(target);
        saveBetweenStations(target);
        sections.add(target);
    }

    private void validateSave(final Section target) {
        List<Station> stations = getAllStations();
        if (isAlreadyExistBothStation(stations, target)) {
            throw new ValidationException(ALREADY_EXISTS_STATIONS_ERROR);
        }
        if (isNotExistsAnyStation(stations, target)) {
            throw new ValidationException(NOT_EXISTS_ANY_STATIONS_ERROR);
        }
    }

    private void saveBetweenStations(Section target) {
        for (Section section : sections) {
            if (isThereAnSameUpStationInTheSections(target)) {
                section.validateSectionDistance(target);
                section.changeUpStation(target.getDownStation());
            }
            if (isThereAnSameDownStationInTheSections(target)) {
                section.validateSectionDistance(target);
                section.changeDownStation(target.getUpStation());
            }
        }
    }

    private boolean isNotExistsAnyStation(final List<Station> stations, final Section target) {
        return !stations.contains(target.getUpStation()) && !stations.contains(target.getDownStation());
    }

    private boolean isAlreadyExistBothStation(final List<Station> stations, final Section target) {
        return stations.contains(target.getUpStation()) && stations.contains(target.getDownStation());
    }

    private boolean isThereAnSameUpStationInTheSections(Section target) {
        return sections.stream()
                .filter(s -> s.getUpStation().equals(target.getUpStation()))
                .collect(Collectors.toList())
                .stream().findFirst()
                .orElse(null) != null;
    }

    private boolean isThereAnSameDownStationInTheSections(Section target) {
        return sections.stream()
                .filter(s -> s.getDownStation().equals(target.getDownStation()))
                .collect(Collectors.toList())
                .stream().findFirst()
                .orElse(null) != null;
    }

    private void validateDelete(final Long downStationId) {
        validateNotExists(downStationId);
        validateSectionMinimumSize();
        validateOnlyTheLastSectionCanBeDelete(downStationId);
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    private void validateNotExists(final Long downStationId) {
        if(isNotExists(downStationId)) {
            throw new NotFoundStationException();
        }
    }

    private boolean isNotExists(final Long id) {
        final List<Station> registeredDownStations = getRegisteredDownStation();
        final List<Station> registeredUpStations = getRegisteredUpStation();
        return !getIds(registeredDownStations).contains(id) && !getIds(registeredUpStations).contains(id);
    }

    private void validateSectionMinimumSize() {
        if (sections.size() <= SECTION_MINIMUM_SIZE) {
            throw new ValidationException(SECTION_MINIMUM_SIZE_ERROR);
        }
    }

    private void validateOnlyTheLastSectionCanBeDelete(final Long downStationId) {
        if (!getLastRegisteredDownStationId().equals(downStationId)) {
            throw new ValidationException(ONLY_THE_LAST_SECTION_CAN_BE_DELETE_ERROR);
        }
    }

    private Long getLastRegisteredDownStationId() {
        return getLastRegisteredDownStation().getId();
    }

    private Station getLastRegisteredDownStation() {
        final List<Station> registeredDownStations = getRegisteredDownStation();
        return  registeredDownStations.get(registeredDownStations.size() - 1);
    }

    private List<Long> getIds(List<Station> registeredStations) {
        return registeredStations.stream()
                .map(Station::getId)
                .collect(Collectors.toList());
    }

    private List<Station> getAllStations() {
        return Stream.concat(getRegisteredDownStation().stream(), getRegisteredUpStation().stream())
                .collect(Collectors.toList());
    }

    private List<Station> getRegisteredDownStation() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private List<Station> getRegisteredUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }

    private List<Station> findUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> findDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }
}
