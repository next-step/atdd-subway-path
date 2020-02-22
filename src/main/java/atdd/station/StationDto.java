package atdd.station;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

        public static Response from(Station station) {
            return Response.builder().id(station.getId()).name(station.getName()).build();
        }
    }
}
