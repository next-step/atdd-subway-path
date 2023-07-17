package nextstep.subway.controller.request;

import nextstep.subway.domain.command.SectionAddCommand;

public class SectionAddRequest implements SectionAddCommand {

    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionAddRequest() {
    }

    public SectionAddRequest(Long upStationId, Long downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
