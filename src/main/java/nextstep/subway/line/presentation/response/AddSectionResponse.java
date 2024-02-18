package nextstep.subway.line.presentation.response;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.service.dto.AddSectionStationDto;

public class AddSectionResponse {

    private Long lineId;

    private Long sectionId;

    private AddSectionStationDto upStation;

    private AddSectionStationDto downStation;

    private Integer distance;

    private AddSectionResponse() {
    }

    private AddSectionResponse(Long lineId, Long sectionId, AddSectionStationDto upStation, AddSectionStationDto downStation, Integer distance) {
        this.lineId = lineId;
        this.sectionId = sectionId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static AddSectionResponse from(Section section) {
        return new AddSectionResponse(
                section.getLine().getLineId(),
                section.getSectionId(),
                AddSectionStationDto.from(section.getUpStation()),
                AddSectionStationDto.from(section.getDownStation()),
                section.getDistance()
        );
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public AddSectionStationDto getUpStation() {
        return upStation;
    }

    public AddSectionStationDto getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

}
