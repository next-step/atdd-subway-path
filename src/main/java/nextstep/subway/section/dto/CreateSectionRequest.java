package nextstep.subway.section.dto;

import lombok.Getter;

@Getter
public class CreateSectionRequest {

    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public CreateSectionRequest(Long upStationId, Long downStationId, Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionDto toDto() {
        return SectionDto.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
    }
}
