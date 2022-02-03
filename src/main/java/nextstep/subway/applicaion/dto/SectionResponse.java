package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;

import java.time.LocalDateTime;

public class SectionResponse {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public SectionResponse(Section section) {
        this.id = section.getId();
        this.lineId = section.getLine().getId();
        this.upStationId = section.getUpStation().getId();
        this.downStationId = section.getDownStation().getId();
        this.distance = section.getDistance();
        this.createdDate = section.getCreatedDate();
        this.modifiedDate = section.getModifiedDate();
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
