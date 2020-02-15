package atdd.station.model.entity;

import atdd.station.converter.StationListConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Table
@Entity
@Getter
public class Line extends BaseEntity {
    private String name;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private int intervalTime;

    @Convert(converter = StationListConverter.class)
    private List<Station> stations = new ArrayList<>();

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
