package atdd.line;

import atdd.station.Station;
import atdd.station.StationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

public class LineDto {

    @Getter
    public static class Request {
        private String name;
        private LocalTime startTime;
        private LocalTime endTime;
        private int intervalTime;
        private int extraFare;

        public Line toEntity() {
            return Line.builder()
                    .name(name)
                    .startTime(startTime)
                    .endTime(endTime)
                    .intervalTime(intervalTime)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Response {

        private String name;
        private LocalTime startTime;
        private LocalTime endTime;
        private int intervalTime;
        private Set<StationDto.Response> stations;

        static LineDto.Response from(Line line, Set<Station> stations) {
            return Response.builder()
                    .name(line.getName())
                    .startTime(line.getStartTime())
                    .endTime(line.getEndTime())
                    .intervalTime(line.getIntervalTime())
                    .stations(stations.stream().map(StationDto.Response::from).collect(Collectors.toSet()))
                    .build();
        }
    }
}
