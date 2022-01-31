package nextstep.subway.domain;

import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.ValidationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nextstep.subway.error.ErrorCode.*;

@Embeddable
public class Sections {

    private static final int SECTION_MINIMUM_SIZE = 1;
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addFirst(Section section) {
        if(!sections.isEmpty()) {
            throw new ValidationException(FIRST_SECTION_CREATE_ERROR);
        }
        sections.add(section);
    }

    public void add(Section section) {
        if(sections.isEmpty()) {
            sections.add(section);
            return;
        }
        save(section);
    }

    public void remove(final Long downStationId) {
        validateDelete(downStationId);
        sections.remove(getLastSection());
    }

    private void save(final Section target) {
        final Section up = findSectionByUpStation(target.getUpStation());
        final Section down = findSectionByDownStation(target.getDownStation());

        validateSave(up, down);

        for (Section section : sections) {
            if(up != null) {
                section.checkTheDistanceToRegisterOrElseThrow(target);
                section.changeUpStation(target.getDownStation());
            }
            if(down != null) {
                section.checkTheDistanceToRegisterOrElseThrow(target);
                section.changeDownStation(target.getUpStation());
            }
        }

        sections.add(target);
    }

    private Section findSectionByUpStation(Station upStation) {
        Optional<Section> section = this.sections.stream()
                .filter(s -> s.getUpStation().equals(upStation))
                .collect(Collectors.toList())
                .stream().findFirst();
        return section.orElse(null);
    }

    private Section findSectionByDownStation(Station downStation) {
        Optional<Section> section = this.sections.stream()
                .filter(s -> s.getDownStation().equals(downStation))
                .collect(Collectors.toList())
                .stream().findFirst();
        return section.orElse(null);
    }

    private void validateSave(final Section up, final Section down) {
        if(isAlreadyExistStations(up, down)) {
            throw new ValidationException(ALREADY_EXISTS_STATIONS_ERROR);
        }
        if(isNotExistsAnyStation(up, down)) {
            throw new ValidationException(NOT_EXISTS_ANY_STATIONS_ERROR);
        }
    }

    private boolean isNotExistsAnyStation(final Section up, final Section down) {
        return up == null && down == null;
    }

    private boolean isAlreadyExistStations(final Section up, final Section down) {
        return up != null && down != null;
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
        final List<Station> registeredDownStations = getRegisteredDownStation(sections);
        final List<Station> registeredUpStations = getRegisteredUpStation(sections);
        return !getIds(registeredDownStations).contains(id) && !getIds(registeredUpStations).contains(id);
    }

    private void validateSectionMinimumSize() {
        if(sections.size() <= SECTION_MINIMUM_SIZE) {
            throw new ValidationException(SECTION_MINIMUM_SIZE_ERROR);
        }
    }

    private void validateOnlyTheLastSectionCanBeDelete(final Long downStationId) {
        if(!getLastRegisteredDownStationId().equals(downStationId)) {
            throw new ValidationException(ONLY_THE_LAST_SECTION_CAN_BE_DELETE_ERROR);
        }
    }

    private Long getLastRegisteredDownStationId() {
        return getLastRegisteredDownStation().getId();
    }

    private Station getLastRegisteredDownStation() {
        final List<Station> registeredDownStations = getRegisteredDownStation(sections);
        return  registeredDownStations.get(registeredDownStations.size() - 1);
    }

    private List<Long> getIds(List<Station> registeredStations) {
        return registeredStations.stream()
                .map(Station::getId)
                .collect(Collectors.toList());
    }

    private List<Station> getRegisteredDownStation(final List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private List<Station> getRegisteredUpStation(final List<Section> sections) {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }
}
