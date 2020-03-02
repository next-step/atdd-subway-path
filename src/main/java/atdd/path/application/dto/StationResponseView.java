package atdd.path.application.dto;

import atdd.path.domain.Line;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class StationResponseView {
    private Long id;
    private String name;
    private List<Line> lines;

    public StationResponseView() {
    }

    @Builder
    public StationResponseView(Long id, String name, List<Line> lines) {
        this.id = id;
        this.name = name;
        this.lines = lines;
    }
}
