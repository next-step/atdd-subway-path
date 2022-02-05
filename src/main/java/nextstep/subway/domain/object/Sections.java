package nextstep.subway.domain.object;

import lombok.val;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> values;

    public Sections() {
        this.values = new ArrayList<>();
    }

    public void add(Section section) {
        if (!validateAddSection(section)) {
            throw new InvalidParameterException();
        }

        updateOriginSectionForAdd(section);
        this.values.add(section);
    }

    private void updateOriginSectionForAdd(Section section) {
        Section originSection = values.stream()
                .filter(value ->
                        value.getUpStation().equals(section.getUpStation())
                ).findFirst()
                .orElse(null);

        if (originSection != null && checkMiddleStationToBeSeperated(section)) {
            Distance changedDistance = originSection.minusDistance(section.getDistance());
            originSection.updateForAdd(section.getDownStation(), changedDistance);
        }
    }

    public int size() {
        return this.values.size();
    }

    public void removeSection(Long stationId) {
        val stations = getAllStations();
        val targetStation = stations.stream()
                .filter(station ->
                        station.getId().equals(stationId)
                ).findFirst()
                .orElse(null);

        if (!validateRemoveSection(targetStation)) {
            throw new InvalidParameterException();
        }

       val targetIndex = stations.indexOf(targetStation);
        if (targetIndex == 0) {
            this.values.remove(getFirstSection());
            return;
        }

        if (targetIndex == stations.size() - 1) {
            this.values.remove(getLastSection());
            return;
        }

        val sectionToBeChanged= values.stream()
                .filter(value ->
                        value.getDownStation().equals(targetStation)
                ).findFirst()
                .orElse(null);

        val targetSection= values.stream()
                .filter(value ->
                        value.getUpStation().equals(targetStation)
                ).findFirst()
                .orElse(null);

        assert sectionToBeChanged != null;
        assert targetSection != null;
        updateOriginSectionForDelete(sectionToBeChanged, targetSection);
        values.remove(targetSection);
    }

    private void updateOriginSectionForDelete(Section preSection, Section targetSection) {
        val changedDistance= preSection.plusDistance(targetSection.getDistance());
        preSection.updateForDelete(targetSection.getDownStation(), changedDistance);
    }

    private boolean validateRemoveSection(Station targetStation) {
        if (isSmallerMinimumSize()) {
            return false;
        }

        return targetStation != null;
    }

    private boolean validateAddSection(Section section) {
        if (values.isEmpty()) {
            return true;
        }

        if (checkDuplicatedSection(section)) {
            return false;
        }

        if (checkLastStationToBeAttached(section)) {
            return true;
        }

        if (checkFirstStationToBeAttached(section)) {
            return true;
        }

        return checkMiddleStationToBeSeperated(section);
    }

    private boolean checkDuplicatedSection(Section section) {
        return this.values.stream()
                .anyMatch(value ->
                        value.getUpStationId().equals(section.getUpStationId())
                                && value.getDownStationId().equals(section.getDownStationId())
                );
    }

    private boolean checkLastStationToBeAttached(Section section) {
        return equalsLastDownStation(section.getUpStationId()) && !checkDuplicatedDownStation(section.getDownStationId());
    }

    private boolean equalsLastDownStation(Long upStationId) {
        return this.lastDownStationId().equals(upStationId);
    }

    private boolean checkDuplicatedDownStation(Long downStationId) {
        return this.values.stream()
                .anyMatch(section ->
                        section.getUpStationId().equals(downStationId) ||
                                section.getDownStationId().equals(downStationId)
                );
    }

    private boolean checkFirstStationToBeAttached(Section section) {
        return equalsFirstUpStation(section.getDownStationId()) && !checkDuplicatedUpStation(section.getDownStationId());
    }

    private boolean equalsFirstUpStation(Long downStationId) {
        return this.getFirstStation().getId().equals(downStationId);
    }

    private boolean checkDuplicatedUpStation(Long getDownStationId) {
        return this.values.stream()
                .anyMatch(section ->
                        section.getUpStationId().equals(getDownStationId) ||
                                section.getDownStationId().equals(getDownStationId)
                );
    }

    private boolean checkMiddleStationToBeSeperated(Section section) {
        return this.values.stream().anyMatch(value ->
                (isSameUpStationNotDownStation(value, section)
                        || isSameDownStationNotUpStation(value, section))
                        && value.getDistanceValue() > section.getDistanceValue()
        );
    }

    private boolean isSameUpStationNotDownStation(Section value, Section section) {
        return value.getUpStationId().equals(section.getUpStationId()) && !value.getDownStationId().equals(section.getDownStationId());
    }

    private boolean isSameDownStationNotUpStation(Section value, Section section) {
        return !value.getUpStationId().equals(section.getUpStationId()) && value.getDownStationId().equals(section.getDownStationId());
    }

    private Section getLastSection() {
        Station lastStation = getLastStation();
        return this.values.stream()
                .filter(value ->
                        value.getDownStation().equals(lastStation)
                ).findFirst()
                .orElse(null);
    }

    private Station getLastStation() {
        List<Station> stations = getAllStations();
        return stations.get(stations.size() - 1);
    }

    private Long lastDownStationId() {
        return getLastSection().getDownStationId();
    }

    public List<Station> getAllStations() {
        Section firstSection = getFirstSection();

        if (firstSection == null) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        stations.add(firstSection.getUpStation());

        return getOrderedStations(stations, firstSection.getDownStation());
    }

    private Section getFirstSection() {
        Station station = getFirstStation();

        if (station == null) {
            return null;
        }

        return this.values.stream()
                .filter(value ->
                        value.getUpStation().equals(station)
                ).findFirst()
                .orElse(null);

    }

    private Station getFirstStation() {
        List<Station> upStations = values.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        List<Station> downStations = values.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return upStations.stream()
                .filter(upStation ->
                        !downStations.contains(upStation)
                ).findFirst()
                .orElse(null);
    }

    private List<Station> getOrderedStations(List<Station> stations, Station downStation) {
        if (downStation == null) {
            return stations;
        }
        stations.add(downStation);

        Station nextDownStation = this.values.stream()
                .filter(value ->
                        value.getUpStation().equals(downStation)
                ).map(Section::getDownStation)
                .findFirst()
                .orElse(null);

        return getOrderedStations(stations, nextDownStation);
    }

    private boolean isSmallerMinimumSize() {
        return this.values.size() <= 1;
    }
}
