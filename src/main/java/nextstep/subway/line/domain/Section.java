package nextstep.subway.line.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    private Long upStationId;

    private Long downStationId;

    private int distance;

    protected Section() {
    }

    public Section(Line line, Long upStationId, Long downStationId, int distance) {
        this.line = line;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean isDownStation(Long stationId) {
        return this.downStationId.equals(stationId);
    }
}