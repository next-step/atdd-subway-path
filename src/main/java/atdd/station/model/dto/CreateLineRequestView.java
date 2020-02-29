package atdd.station.model.dto;

import atdd.station.model.entity.Line;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CreateLineRequestView {
    private String name;

    private LocalTime startTime;

    private LocalTime endTime;

    private int intervalTime;

    public CreateLineRequestView() {
    }

    @Builder
    private CreateLineRequestView(final String name,
                                  final LocalTime startTime,
                                  final LocalTime endTime,
                                  final int intervalTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }

    public Line toLine() {
        return Line.builder()
                .name(this.name)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .intervalTime(this.intervalTime)
                .build();
    }

}
