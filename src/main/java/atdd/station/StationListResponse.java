package atdd.station;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class StationListResponse {
    private List<Station> stations;

    public StationListResponse () {

    }

    @Builder
    public StationListResponse(List<Station> stations){
       this.stations= stations;
    }

    public static StationListResponse of(List<Station> stations){
        return StationListResponse.builder()
                .stations(stations)
                .build();
    }
}
