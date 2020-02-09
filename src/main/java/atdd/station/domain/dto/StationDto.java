package atdd.station.domain.dto;

import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StationDto
{
    private String name;

    public Station toEntity()
    {
       return Station.builder().name(name).build();
    }
}
