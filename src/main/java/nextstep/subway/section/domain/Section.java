package nextstep.subway.section.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    @Builder
    public Section(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public void changeDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public void changeDistance(int distance) {
        this.distance = distance;
    }
}