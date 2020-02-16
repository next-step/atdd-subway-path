package atdd.station.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEdgeRequestView {
    private long sourceStationId;
    private long targetStationId;
}
