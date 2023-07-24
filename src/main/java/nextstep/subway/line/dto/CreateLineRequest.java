package nextstep.subway.line.dto;
import lombok.Value;

@Value
public
class CreateLineRequest {
    String name;
    String color;
    Long upStationId;
    Long downStationId;
    Long distance;
}
