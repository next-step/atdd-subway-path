package atdd.line;

import atdd.station.StationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import atdd.station.Station;
import lombok.Builder;

import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class LineDetailResponse {
    private Line line;
    private List<StationResponse> stations;

    @Builder
    public LineDetailResponse(Line entity, List<StationResponse> stations){
        this.line = entity;
        this.stations = stations;
    }

    public static LineDetailResponse of(Line entity, List<StationResponse> stations){
        return LineDetailResponse.builder()
                .entity(entity)
                .stations(stations)
                .build();
    }


}
