package atdd.line.api.response;

import atdd.line.domain.Line;
import atdd.station.api.response.StationResponseView;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class LineResponseView {

    private Long id;
    private String name;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private int intervalTime;

    private List<StationResponseView> stations;

    public LineResponseView(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.startTime = line.getStartTime();
        this.endTime = line.getEndTime();
        this.intervalTime = line.getIntervalTime();
        this.stations = line.getStations().stream().map(StationResponseView::new).collect(toList());
    }

}
