package nextstep.subway.linesection;

import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.*;

@Entity
@Table(name = "line_section")
public class LineSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;
    private Integer distance;

    public static LineSection of(Line line, Station upStation, Station downStation, Integer distance) {
        LineSection lineSection = new LineSection();
        lineSection.line = line;
        lineSection.upStation = upStation;
        lineSection.downStation = downStation;
        lineSection.distance = distance;
        return lineSection;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public Line getLine() {
        return line;
    }
}
