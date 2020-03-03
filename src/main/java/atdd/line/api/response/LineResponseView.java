package atdd.line.api.response;

import atdd.line.domain.Line;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class LineResponseView {

    private Long id;
    private String name;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private int intervalTime;

    private List<LineStationResponse> stations;

    public LineResponseView(Line line) {
        this(line, emptyList());
    }

    public LineResponseView(Line line, List<LineStationResponse> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.startTime = line.getStartTime();
        this.endTime = line.getEndTime();
        this.intervalTime = line.getIntervalTime();
        this.stations = stations;
    }

}
