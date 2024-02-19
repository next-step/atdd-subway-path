package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Section implements Comparable<Section> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    // TODO 메소드 네이밍 변경, 로직 이관
    public void updateDownStation(Station upStation, int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 거리는 양수여야 합니다.");
        }

        this.downStation = upStation;
        this.distance = distance;
    }

    // TODO 메소드 네이밍 변경, 로직 이관
    public void updateUpStation(Station downStation, int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 거리는 양수여야 합니다.");
        }

        this.upStation = downStation;
        this.distance = distance;
    }

    public boolean isDownStation(Section other) {
        return downStation.getId().equals(other.getDownStation().getId());
    }

    public boolean isUpStation(Section other) {
        return upStation.getId().equals(other.getUpStation().getId());
    }

    @Override
    public int compareTo(Section other) {
        if (this.getUpStation().getId().equals(other.getUpStation().getId()) &&
            this.getDownStation().getId().equals(other.getDownStation().getId())) {
            return 0;
        }

        return this.getDownStation().getId().equals(other.getUpStation().getId()) ? -1 : 1;
    }
}