package nextstep.subway.line;

import nextstep.subway.Station;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class LineStations {

    @OneToMany
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public boolean isLastStation(Station station) {
        Optional<LineStation> optionalSection = this.lineStations
                .stream()
                .filter(LineStation::isLastStation)
                .findFirst();

        return optionalSection.map(lineStation -> lineStation.getStation().equals(station)).orElse(true);
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    private Optional<LineStation> findLastLineStation() {
        return this.lineStations
                .stream()
                .filter(LineStation::isLastStation)
                .findFirst();
    }

    public LineStation addSection(LineStation lineStation) {
        findLastLineStation().ifPresent(e -> e.setNextLineStation(lineStation));
        lineStations.add(lineStation);
        return lineStation;
    }

    public List<Station> getStations() {
        return lineStations.stream()
                .map(LineStation::getStation)
                .collect(java.util.stream.Collectors.toList());
    }

    public LineStation removeSection(Station station) {
        if (!isLastStation(station)) {
            throw new IllegalArgumentException("삭제할 수 없는 구간입니다. 마지막 구간이 아닙니다.");
        }

        LineStation lastLineStation = findLastLineStation()
                .orElseThrow(() -> new IllegalArgumentException("마지막 구간이 존재하지 않습니다."));

        lineStations.stream()
                .filter(lineStation -> lineStation.getNextLineStation().getStation().equals(station))
                .findFirst()
                .ifPresent(LineStation::removeNextLineStation);

        lineStations.remove(lastLineStation);

        return lastLineStation;
    }

    public long countOfStations() {
        return lineStations.size();
    }
}
