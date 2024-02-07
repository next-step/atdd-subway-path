package nextstep.subway.service.dto;

import nextstep.subway.domain.Section;

public class LineSectionDto {
    private Long id;
    private StationDto upStation;
    private StationDto downStation;
    private int distance;

    public LineSectionDto(Long id, StationDto upStation, StationDto downStation, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public StationDto getUpStation() {
        return upStation;
    }

    public StationDto getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public static LineSectionDto from(Section section) {
        StationDto upStation = StationDto.from(section.getUpStation());
        StationDto downStation = StationDto.from(section.getDownStation());
        return new LineSectionDto(section.getId(), upStation, downStation, section.getDistance());
    }
}
