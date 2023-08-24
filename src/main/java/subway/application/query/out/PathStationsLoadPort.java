package subway.application.query.out;

import lombok.Getter;
import subway.domain.PathStation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface PathStationsLoadPort {
    PathStationsLoadPort.Response findAllBy(List<PathStation.Id> pathStationIds);

    class Response {
        Map<PathStation.Id, PathStation> pathStations;

        private Response(Map<PathStation.Id, PathStation> pathStations) {
            this.pathStations = pathStations;
        }

        public static Response from(List<PathStation> pathStations) {
            Map<PathStation.Id, PathStation> idToPathStationMap =
                    pathStations.stream().collect(Collectors.toMap(PathStation::getId, Function.identity()));
            return new Response(idToPathStationMap);
        }

        public Optional<PathStation> getStationBy(PathStation.Id pathStationId) {
            return Optional.ofNullable(pathStations.get(pathStationId));
        }

    }
}
