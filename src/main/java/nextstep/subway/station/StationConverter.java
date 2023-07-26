package nextstep.subway.station;

import org.springframework.stereotype.Component;

@Component
public class StationConverter {
    public StationResponse convert(Station station) {
        if(station==null)
            return null;
        return new StationResponse(station.getId(), station.getName());
    }
}
