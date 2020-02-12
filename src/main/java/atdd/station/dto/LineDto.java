package atdd.station.dto;

import atdd.station.domain.Line;
import atdd.station.domain.Subway;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LineDto
{
    private String name;
    private String startTime;
    private String endTime;
    private String intervalTime;
    private List<Subway> subways;

    public Line toEntity()
    {
        return Line.builder()
                .name(name)
                .subways(subways)
                .build();
    }

    public Line toEntity(String name, List<Subway> subways)
    {
        return Line.builder()
                .name(name)
                .subways(subways)
                .build();
    }
}
