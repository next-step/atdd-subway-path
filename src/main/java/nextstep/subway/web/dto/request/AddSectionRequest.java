package nextstep.subway.web.dto.request;


import lombok.Getter;

@Getter
public class AddSectionRequest {

    private final Long upStationId;

    private final Long downStationId;

    private final Long distance;

    public AddSectionRequest(Long upStationId, Long downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
