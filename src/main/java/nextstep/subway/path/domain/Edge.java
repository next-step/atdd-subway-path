package nextstep.subway.path.domain;

import lombok.Getter;

@Getter
public class Edge {

    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Edge(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
