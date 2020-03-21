package atdd.path.dto;

import atdd.path.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.sound.sampled.Line;
import java.util.List;

@Getter
@NoArgsConstructor
public class StationResponseView {
    private Long id;
    private String name;
    private List<Line> lines;

    @Builder
    public StationResponseView(Long id, String name, List<Line> lines) {
        this.id = id;
        this.name = name;
        this.lines = lines;
    }

    public static StationResponseView of(Station station){
        return StationResponseView.builder()
                .id(station.getId())
                .name(station.getName())
                .build();
    }
}
