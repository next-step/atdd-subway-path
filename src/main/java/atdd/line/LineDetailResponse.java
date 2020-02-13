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
    private LineResponse line;
    private List<StationResponse> stations;

    @Builder
    public LineDetailResponse(LineResponse line, List<StationResponse> stations){
        this.line = line;
        this.stations = stations;
    }

    public static LineDetailResponse of(LineResponse line, List<StationResponse> stations){
        return LineDetailResponse.builder()
                .line(line)
                .stations(stations)
                .build();
    }


}
