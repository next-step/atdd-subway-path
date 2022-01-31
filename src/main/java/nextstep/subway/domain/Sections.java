package nextstep.subway.domain;

import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.ValidationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
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
        validateSave(section.getUpStation().getId(), section.getDownStation().getId());
        sections.add(section);
    }

    public void remove(final Long downStationId) {
        validateDelete(downStationId);
        sections.remove(getLastSection());
    }

    private void validateSave(final Long upStationId, final Long downStationId) {
        if(sections.isEmpty()) {
            return;
        }

        final List<Station> registeredDownStations = getRegisteredDownStation(sections);
        final List<Station> registeredUpStations = getRegisteredUpStation(sections);

        upStationMustSameLastRegisteredDownStation(registeredDownStations, upStationId);
        downStationMustNotCurrentlyRegisteredStation(registeredDownStations, registeredUpStations, downStationId);
    }

    private void validateDelete(final Long downStationId) {
        validateNotExists(downStationId);
        validateSectionMinimumSize();
        validateOnlyTheLastSectionCanBeDelete(downStationId);
    }

    private void upStationMustSameLastRegisteredDownStation(
            final List<Station> registeredDownStations,
            final Long upStationId
    ) {
        final Station lastRegisteredDownStation = registeredDownStations.get(registeredDownStations.size() - 1);
        if(!lastRegisteredDownStation.getId().equals(upStationId)) {
            throw new ValidationException(UP_STATION_MUST_SAME_LAST_REGISTERED_DOWN_STATION_ERROR);
        }
    }

    private void downStationMustNotCurrentlyRegisteredStation(
            final List<Station> registeredDownStations,
            final List<Station> registeredUpStations,
            final Long downStationId
    ) {
        if(getIds(registeredDownStations).contains(downStationId) || getIds(registeredUpStations).contains(downStationId)) {
            throw new ValidationException(DOWN_STATION_MUST_NOT_CURRENTLY_REGISTERED_STATION_ERROR);
        }
    }

    private void validateNotExists(final Long downStationId) {
        if(isNotExists(downStationId)) {
            throw new NotFoundStationException();
        }
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

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    private boolean isNotExists(Long id) {
        final List<Station> registeredDownStations = getRegisteredDownStation(sections);
        final List<Station> registeredUpStations = getRegisteredUpStation(sections);
        return !getIds(registeredDownStations).contains(id) && !getIds(registeredUpStations).contains(id);
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
