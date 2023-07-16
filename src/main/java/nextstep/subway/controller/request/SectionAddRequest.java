package nextstep.subway.controller.request;

import nextstep.subway.service.command.SectionAddCommand;

public class SectionAddRequest implements SectionAddCommand {

    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionAddRequest() {
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
