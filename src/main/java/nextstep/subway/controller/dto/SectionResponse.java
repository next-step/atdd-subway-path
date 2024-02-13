package nextstep.subway.controller.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.Section;

import static nextstep.subway.controller.dto.StationResponse.stationToStationResponse;

@Getter
public class SectionResponse {
    private Long id;
    private Long lineId;
    private StationResponse upStations;
    private StationResponse downStations;
    private Long distance;

    @Builder
    public SectionResponse(Long id, Long lineId, StationResponse upStations, StationResponse downStations, Long distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStations = upStations;
        this.downStations = downStations;
        this.distance = distance;
    }

    public static SectionResponse sectionToSectionResponse(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .lineId(section.getLine().getId())
                .upStations(stationToStationResponse(section.getUpStation()))
                .downStations(stationToStationResponse(section.getDownStation()))
                .distance(section.getDistance())
                .build();
    }

}
