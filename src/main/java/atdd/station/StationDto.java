package atdd.station;

import atdd.line.Line;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

public class StationDto {

    @Getter
    public static class Request {

        private String name;

        Station toEntity() {
            return Station.of(name);
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Getter
    public static class Response {

        private Long id;
        private String name;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Set<LineDto> lines;

        public static Response from(Station station) {
            return Response.builder()
                    .id(station.getId())
                    .name(station.getName())
                    .build();
        }

        public static Response from(Station station, Set<Line> lines) {
            return Response.builder()
                    .id(station.getId())
                    .name(station.getName())
                    .lines(lines.stream().map(LineDto::from).collect(Collectors.toSet()))
                    .build();
        }

        @Getter
        @Builder
        static class LineDto {

            private Long id;
            private String name;

            static LineDto from(Line line) {
                return LineDto.builder()
                        .id(line.getId())
                        .name(line.getName())
                        .build();
            }
        }
    }
}
