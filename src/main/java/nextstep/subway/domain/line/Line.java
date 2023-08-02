package nextstep.subway.domain.line;

import nextstep.subway.domain.station.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LineDetail lineDetail;

    @Embedded
    private LineStationDetail lineStationDetail;

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.lineDetail = new LineDetail(name, color);
        this.lineStationDetail = new LineStationDetail(this, upStation, downStation, distance);
    }

    public void modify(String name, String color) {
        lineDetail.modify(name, color);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        lineStationDetail.addSection(new Section(this, upStation, downStation, distance));
    }

    public void removeSection(Station station) {
        lineStationDetail.removeSection(station);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return lineDetail.getName();
    }

    public String getColor() {
        return lineDetail.getColor();
    }

    public List<Station> getStations() {
        return lineStationDetail.getStations();
    }

    public List<Section> getSections() {
        return lineStationDetail.getSections();
    }

}
