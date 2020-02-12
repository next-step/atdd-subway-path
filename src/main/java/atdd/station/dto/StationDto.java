package atdd.station.dto;

import atdd.station.domain.Station;
import atdd.station.domain.Subway;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StationDto
{
    private String name;
    private List<Subway> subways;

    public Station toEntity()
    {
       return Station.builder()
               .name(name)
               .build();
    }
}
