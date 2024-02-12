package nextstep.subway.section.presentation.response;

import nextstep.subway.station.service.StationDto;

import java.util.List;

public class SectionResponse {

    private Long sectionId;
    private Integer distance;
    private List<StationDto> stations;

    public SectionResponse(Long sectionId, Integer distance, List<StationDto> stationDtos) {
        this.sectionId = sectionId;
        this.distance = distance;
        this.stations = stationDtos;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public Integer getDistance() {
        return distance;
    }

    public List<StationDto> getStations() {
        return stations;
    }

}
