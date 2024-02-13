package nextstep.subway.line;

import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;


public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private Station startStation;
    private Station endStation;

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    private Sections sections;


    public static LineResponse of(Line line){
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getSections(), line.getStartStation(), line.getEndStation());
    }

    private LineResponse(Long id, String name, String color, Sections sections, Station startStation, Station endStation) {

        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
        this.startStation = startStation;
        this.endStation = endStation;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }
}
