package atdd.station;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class StationDto {
    String name;

    @Builder(builderClassName = "responseBuilder", builderMethodName = "responseBuilder")
    public StationDto(Station station) {
        this.name = station.getName();
    }
}
