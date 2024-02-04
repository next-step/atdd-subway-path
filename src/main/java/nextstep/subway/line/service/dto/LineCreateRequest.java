package nextstep.subway.line.service.dto;

import nextstep.subway.line.exception.CreateRequestNotValidException;
import org.springframework.util.StringUtils;

import java.util.Objects;

public class LineCreateRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private LineCreateRequest() {
    }

    public LineCreateRequest(final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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

    public void validate() {
        if(!StringUtils.hasLength(name)) {
            throw new CreateRequestNotValidException("name can not be empty");
        }
        if(!StringUtils.hasLength(color)) {
            throw new CreateRequestNotValidException("color can not be empty");
        }
        if(Objects.isNull(upStationId)) {
            throw new CreateRequestNotValidException("upStationId can not be null");
        }
        if(Objects.isNull(downStationId)) {
            throw new CreateRequestNotValidException("downStationId can not be null");
        }
        if(downStationId.equals(upStationId)) {
            throw new CreateRequestNotValidException("upStationId and downStationId can not be the same");
        }
        if(distance <= 0) {
            throw new CreateRequestNotValidException("distance must be greater than 0");
        }
    }

    public SectionCreateRequest toSectionCreateRequest() {
        return new SectionCreateRequest(this.upStationId, this.downStationId, this.distance);
    }
}
