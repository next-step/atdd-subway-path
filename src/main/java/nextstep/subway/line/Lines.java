package nextstep.subway.line;

import nextstep.subway.station.Station;

import java.util.List;
import java.util.function.Consumer;

public class Lines {

    private final List<Line> lineList;

    private Lines(List<Line> lineList) {
        this.lineList = lineList;
    }

    public static Lines from(List<Line> lineList) {
        return new Lines(List.copyOf(lineList));
    }

    public boolean existStation(Station station) {
        return this.lineList.stream().anyMatch(line -> line.existStation(station));
    }

    public void forEach(Consumer<Line> action) {
        lineList.forEach(action);
    }
}
