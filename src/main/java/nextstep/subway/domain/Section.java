package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @Column(name = "up_station_id", nullable = false)
    private Long upStationId;

    @Column(name = "down_station_id", nullable = false)
    private Long downStationId;

    @Column(name = "distance", nullable = false)
    private int distance;

    protected Section() {
    }

    public Section(
        Line line,
        Long upStationId,
        Long downStationId,
        int distance
    ) {
        if (upStationId == null || downStationId == null || distance == 0) {
            throw new IllegalArgumentException();
        }
        this.line = line;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean isSameDownStation(Long stationId) {
        return this.downStationId.equals(stationId);
    }

    public boolean isConnectable(Section other) {
        return this.downStationId.equals(other.upStationId);
    }

    public boolean containsLastStation(Section other) {
        return this.upStationId.equals(other.downStationId) || this.downStationId.equals(other.downStationId);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
