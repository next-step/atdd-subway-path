package nextstep.subway.line.view;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineCreateRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int distance;
}
