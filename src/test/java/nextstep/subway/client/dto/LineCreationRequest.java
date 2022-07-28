package nextstep.subway.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineCreationRequest {

    private String name;

    private String color;

    private Long upStationId;

    private Long downStationId;

    private int distance;

}
