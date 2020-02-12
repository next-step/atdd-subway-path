package atdd.station;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class StationDto {

    @Getter
    @Setter
    public static class Request {

        private String name;

        Station toEntity() {
            return Station.of(name);
        }
    }

    @EqualsAndHashCode
    @ToString
    @Builder
    @Getter
    @AllArgsConstructor
    @Setter
    @NoArgsConstructor
    public static class Response {

        private String name;

        static Response from(Station station) {
            return Response.builder().name(station.getName()).build();
        }
    }
}
