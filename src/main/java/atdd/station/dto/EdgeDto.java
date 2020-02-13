package atdd.station.dto;

import atdd.station.domain.Edge;
import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EdgeDto
{
    private Station source;
    private Station target;

    public Edge toEntity()
    {
        return Edge.builder()
                .source(source)
                .target(target)
                .build();
    }

}
