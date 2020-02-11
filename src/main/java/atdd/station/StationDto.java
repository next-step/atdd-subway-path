package atdd.station;

import lombok.Getter;
import lombok.Setter;

public class StationDto {

    @Getter
    @Setter
    public static class Request {

        private String name;

        Station toEntity() {
            return Station.builder()
                    .name(StationName.builder()
                            .name(name)
                            .build())
                    .build();
        }
    }
}
