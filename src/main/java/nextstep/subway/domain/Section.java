package nextstep.subway.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long upStationId;

    private Long downStationId;

    private int distance;

    protected Section() {
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean isDownStation(Long stationId) {
        return this.downStationId.equals(stationId);
    }
}