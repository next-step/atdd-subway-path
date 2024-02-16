package nextstep.subway.section;

import static org.springframework.util.Assert.notNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Section {

    private final static String REQUIRED_UP_STATION = "상행역은 필수값입니다.";
    private final static String REQUIRED_DOWN_STATION = "하행역은 필수값입니다.";
    private final static String REQUIRED_DISTANCE = "구간의 거리는 필수값입니다.";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public boolean lastAddValidate(Section section) { // 여기 파라미터로 들어오는게 기존 역
        return downStationId.equals(section.downStationId) || downStationId.equals(section.upStationId);
    }

    public boolean firstAddValidate(Section section) { // 여기 파라미터로 들어오는게 기존 역
        return upStationId.equals(section.downStationId) || upStationId.equals(section.upStationId);
    }

    public Section() {
    }

    public Section(Long upStationId, Long downStationId, Long distance) {
        validateSection(upStationId, downStationId, distance);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private static void validateSection(Long upStationId, Long downStationId, Long distance) {
        notNull(upStationId, REQUIRED_UP_STATION);
        notNull(downStationId, REQUIRED_DOWN_STATION);
        notNull(distance, REQUIRED_DISTANCE);
    }

    public Long getId() {
        return id;
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


    public void updateUpStationId(Long downStationId) {
        this.upStationId = downStationId;
    }

    public void updateDistance(Long distance) {
        this.distance = distance;
    }
}
