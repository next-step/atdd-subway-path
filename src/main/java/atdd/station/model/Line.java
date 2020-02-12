package atdd.station.model;

import lombok.Builder;

import java.util.Date;
import java.util.List;

public class Line {
    private long id;
    private String name;
    private Date startTime;
    private Date endTime;
    private Date intervalTime;
    private List<Station> stations;

    @Builder
    public Line(long id,
                String name,
                Date startTime,
                Date endTime,
                Date intervalTime,
                List<Station> stations){
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.stations = stations;
    }
}
