package subway.db.h2.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "subway_sections")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubwaySectionJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subway_section_id")
    private Long id;
    @Column(nullable = false)
    private Long upStationId;
    @Column(nullable = false)
    private String upStationName;
    @Column(nullable = false)
    private Long downStationId;
    @Column(nullable = false)
    private String downStationName;
    @Column(nullable = false)
    private BigDecimal distance;


    private SubwaySectionJpa(Long upStationId, String upStationName, Long downStationId, String downStationName, BigDecimal distance) {
        this.upStationId = upStationId;
        this.upStationName = upStationName;
        this.downStationId = downStationId;
        this.downStationName = downStationName;
        this.distance = distance;
    }

    private SubwaySectionJpa(Long id, Long upStationId, String upStationName, Long downStationId, String downStationName, BigDecimal distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.upStationName = upStationName;
        this.downStationId = downStationId;
        this.downStationName = downStationName;
        this.distance = distance;
    }

    public static SubwaySectionJpa register(Long upStationId, String upStationName, Long downStationId, String downStationName, BigDecimal distance) {
        return new SubwaySectionJpa(upStationId, upStationName, downStationId, downStationName, distance);
    }

    public static SubwaySectionJpa of(Long id, Long upStationId, String upStationName, Long downStationId, String downStationName, BigDecimal distance) {
        return new SubwaySectionJpa(id, upStationId, upStationName, downStationId, downStationName, distance);
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public String getUpStationName() {
        return upStationName;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public String getDownStationName() {
        return downStationName;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public boolean isNew() {
        return id == null;
    }

    public void update(Long upStationId, String upStationName, Long downStationId, String downStationName, BigDecimal distance) {
        this.upStationId = upStationId;
        this.upStationName = upStationName;
        this.downStationId = downStationId;
        this.downStationName = downStationName;
        this.distance = distance;
    }
}
