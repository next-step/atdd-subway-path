package atdd.station.model.entity;

import atdd.station.converter.StationConverter;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalTime;
import java.util.List;

@Table
@Entity
@Getter
public class Line extends BaseEntity {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;

    private int intervalTime;

    @Convert(converter = StationConverter.class)
    private List<Station> stations;

    public Line() {
    }

    @Builder
    private Line(final String name,
                 final LocalTime startTime,
                 final LocalTime endTime,
                 final int intervalTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }
}
