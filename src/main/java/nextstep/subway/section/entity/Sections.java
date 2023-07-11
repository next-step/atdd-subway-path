package nextstep.subway.section.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.InvalidLineSectionException;
import nextstep.subway.station.entity.Station;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sections {

    @ElementCollection
    @CollectionTable(name = "section", joinColumns = @JoinColumn(name = "line_id"))
    private List<Section> sections = new ArrayList<>();

    private Integer totalDistance;

    public void addSection(Section section) {
        if (!getLastStation().equals(section.getUpStation())) {
            throw new InvalidLineSectionException(ErrorCode.INVALID_UP_STATION);
        }

        if (getAllStations().contains(section.getDownStation())) {
            throw new InvalidLineSectionException(ErrorCode.ALREADY_REGISTERED_STATION);
        }

        this.sections.add(section);
        this.totalDistance += section.getDistance();
    }

    public void deleteSectionByStationId(Long stationId) {
        List<Long> allStationIds = getAllStations()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());
        System.out.println("stationId = " + stationId);
        for (Long id : allStationIds){
            System.out.println("id = " + id);
        }
        if (!allStationIds.contains(stationId)) {
            throw new InvalidLineSectionException(ErrorCode.UNREGISTERED_STATION);
        }

        if (sections.size() == 1) {
            throw new InvalidLineSectionException(ErrorCode.STAND_ALONE_LINE_SECTION);
        }

        if (!getLastStation().getId().equals(stationId)) {
            throw new InvalidLineSectionException(ErrorCode.IS_NOT_LAST_LINE_SECTION);
        }

        this.sections = sections.stream()
                .filter(section -> {
                    boolean isDeleteTarget = section.getDownStation().getId().equals(stationId);
                    if (isDeleteTarget) {
                        this.totalDistance -= section.getDistance();
                    }

                    return !isDeleteTarget;
                })
                .collect(Collectors.toList());
    }

    public List<Station> getAllStations() {
        List<Station> stations = this.sections
                .stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        int lastIndex = sections.size() - 1;
        stations.add(sections.get(lastIndex).getDownStation());

        return stations;
    }

    private Station getLastStation() {
        int lastIndex = sections.size() - 1;
        return sections.get(lastIndex).getDownStation();
    }

}
