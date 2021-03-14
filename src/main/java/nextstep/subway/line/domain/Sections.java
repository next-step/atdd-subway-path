package nextstep.subway.line.domain;

import nextstep.subway.exception.NoOtherStationException;
import nextstep.subway.exception.NotEqualsNameException;
import nextstep.subway.exception.SubwayNameDuplicateException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {

    }

    private int size() {
        return sections.size();
    }

    private Section getFinishSection() {
        return sections.get(size() - 1);
    }

    private Station getDownStation() {
        return sections.get(size() - 1).getDownStation();
    }

    private boolean isEqualsStation(Section section, StationResponse station) {
        String upStationName = section.getUpStationName();
        String downStationName = station.getName();
        return upStationName.equals(downStationName);
    }

    private boolean isExistedDownStation(List<StationResponse> stations, Section section) {
        return stations.stream().anyMatch(i -> i.getId().equals(section.getDownStationId()));
    }

    private void addSectionValidate(Section section, List<StationResponse> stations) {
        StationResponse downStation = stations.get(1);

        if (!isEqualsStation(section, downStation)) {
            throw new NotEqualsNameException();
        }

        if (isExistedDownStation(stations, section)) {
            throw new SubwayNameDuplicateException();
        }
    }

    private boolean isNotOtherStation() {
        return size() == 1;
    }

    private boolean isTarget(long stationId) {
        return Objects.equals(getDownStation().getId(), stationId);
    }

    private void isValidDeleteSection(long stationId) {
        if (!isTarget(stationId)) {
            throw new IllegalArgumentException("종점역만 삭제가 가능합니다.");
        }

        if (isNotOtherStation()) {
            throw new NoOtherStationException();
        }
    }

    public void addSection(Section section) {
        List<StationResponse> stations = getAllStation();
        if (stations.size() == 0) {
            sections.add(section);
            return;
        }

        addSectionValidate(section, stations);

        sections.add(section);
    }

    public void deleteLastSection(Long stationId) {
        isValidDeleteSection(stationId);
        sections.remove(getFinishSection());
    }

    public List<StationResponse> getAllStation() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<StationResponse> responses = new ArrayList<>();
        responses.add(StationResponse.of(sections.get(0).getUpStation()));

        sections.stream().map(section -> StationResponse.of(section.getDownStation())).forEach(responses::add);
        return responses;
    }
}
