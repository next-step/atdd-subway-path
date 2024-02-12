package nextstep.subway.line.presentation.response;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.service.StationDto;

public class AddSectionResponse {

    private Long lineId;

    private Long sectionId;

    private StationDto upStation;

    private StationDto downStation;

    private Integer distance;

    private AddSectionResponse() {
    }

    private AddSectionResponse(Long lineId, Long sectionId, StationDto upStation, StationDto downStation, Integer distance) {
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
                StationDto.from(section.getUpStation()),
                StationDto.from(section.getDownStation()),
                section.getDistance()
        );
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public StationDto getUpStation() {
        return upStation;
    }

    public StationDto getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

}
