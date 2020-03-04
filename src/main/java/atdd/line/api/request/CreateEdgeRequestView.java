package atdd.line.api.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class CreateEdgeRequestView {

    private int elapsedTime;
    private int distance;
    private Long sourceStationId;
    private Long targetStationId;

}
