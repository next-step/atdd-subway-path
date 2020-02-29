package atdd.station.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEdgeRequestView {
    private long sourceStationId;
    private long targetStationId;
}
