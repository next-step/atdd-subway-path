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
        this(
            null,
            line,
            upStationId,
            downStationId,
            distance
        );
    }

    public Section(
        Long id,
        Line line,
        Long upStationId,
        Long downStationId,
        int distance
    ) {
        if (upStationId == null || downStationId == null || distance == 0) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.line = line;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean isSameDownStation(Long stationId) {
        return this.downStationId.equals(stationId);
    }

    public boolean isSameUpStation(Long stationId) {
        return this.upStationId.equals(stationId);
    }

    public boolean containsStation(Section other) {
        return containsUpStation(other) || containsDownStation(other);
    }

    public boolean containsUpStation(Section other) {
        return this.upStationId.equals(other.upStationId) || this.downStationId.equals(other.upStationId);
    }

    public boolean containsDownStation(Section other) {
        return this.upStationId.equals(other.downStationId) || this.downStationId.equals(other.downStationId);
    }

    public boolean isDownConnected(Section other) {
        return this.downStationId.equals(other.upStationId);
    }

    public Section nextSection(Section other) {
        if (this.distance <= other.distance) {
            throw new IllegalArgumentException(
                String.format("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음 Section: %s", other)
            );
        }
        return new Section(
            this.line,
            other.downStationId,
            this.downStationId,
            this.distance - other.distance
        );
    }

    public Section mergeSection(Section other) {
        return new Section(
            line,
            this.upStationId,
            other.downStationId,
            this.distance + other.distance
        );
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
        return distance == section.distance &&
            Objects.equals(line, section.line) &&
            Objects.equals(upStationId, section.upStationId) &&
            Objects.equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", line=" + line +
            ", upStationId=" + upStationId +
            ", downStationId=" + downStationId +
            ", distance=" + distance +
            '}';
    }
}
