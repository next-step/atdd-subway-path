package subway.application.command.out;

import lombok.Builder;
import subway.domain.Station;

import java.util.List;
import java.util.Map;

public interface StationMapLoadByInPort {
    Map<Station.Id, Station> findAllByIn(List<Station.Id> stationIds);


    class Response {

        private final Station upStation;
        private final Station downStation;

        @Builder
        private Response(Station upStation, Station downStation) {
            this.upStation = upStation;
            this.downStation = downStation;
        }

        public static Response of(Station upStation, Station downStation) {
            return Response.builder()
                    .upStation(upStation)
                    .downStation(downStation)
                    .build();
        }

        public Station getUpStation() {
            return upStation;
        }

        public Station getDownStation() {
            return downStation;
        }
    }
}

