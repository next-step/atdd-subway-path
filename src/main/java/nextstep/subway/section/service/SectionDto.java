package nextstep.subway.section.service;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.service.StationDto;

public class SectionDto {

    private Long sectionId;
    private StationDto upStation;
    private StationDto downStation;
    private Integer distance;

    private SectionDto() {
    }

    private SectionDto(Long sectionId, StationDto upStation, StationDto downStation, Integer distance) {
        this.sectionId = sectionId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionDto from(Section section) {
        return new SectionDto(
                section.getSectionId(),
                StationDto.from(section.getUpStation()),
                StationDto.from(section.getDownStation()),
                section.getDistance()
        );
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
