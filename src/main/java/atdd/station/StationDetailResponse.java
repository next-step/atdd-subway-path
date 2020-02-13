package atdd.station;

import atdd.line.Line;
import atdd.line.LineResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class StationDetailResponse {
    private Station station;
    private List<LineResponse> lines;

    @Builder
    public StationDetailResponse(Station station, List<LineResponse> lines){
        this.station = station;
        this.lines = lines;
    }

    public static StationDetailResponse of(Station station, List<LineResponse> lines){
        return StationDetailResponse.builder()
                .station(station)
                .lines(lines)
                .build();
    }
}
