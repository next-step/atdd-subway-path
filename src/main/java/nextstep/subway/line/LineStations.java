package nextstep.subway.line;

import nextstep.subway.station.Station;

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
    private List<Section> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public LineStations(List<Section> lineStations) {
        this.lineStations = lineStations;
    }

    public boolean isLastStation(Station station) {
        Optional<Section> optionalSection = this.lineStations
                .stream()
                .filter(Section::isLastStation)
                .findFirst();

        return optionalSection.map(lineStation -> lineStation.getUpStation().equals(station)).orElse(true);
    }

    public List<Section> getLineStations() {
        return lineStations;
    }

    private Optional<Section> findLastLineStation() {
        return this.lineStations
                .stream()
                .filter(Section::isLastStation)
                .findFirst();
    }

    public Section addSection(Section lineStation) {
        findLastLineStation().ifPresent(e -> e.setDownStation(lineStation));
        lineStations.add(lineStation);
        return lineStation;
    }

    public List<Station> getStations() {
        return lineStations.stream()
                .map(Section::getUpStation)
                .collect(java.util.stream.Collectors.toList());
    }

    public Section removeSection(Station station) {
        if (!isLastStation(station)) {
            throw new IllegalArgumentException("삭제할 수 없는 구간입니다. 마지막 구간이 아닙니다.");
        }

        Section lastLineStation = findLastLineStation()
                .orElseThrow(() -> new IllegalArgumentException("마지막 구간이 존재하지 않습니다."));

        lineStations.stream()
                .filter(lineStation -> lineStation.getDownStation().getUpStation().equals(station))
                .findFirst()
                .ifPresent(Section::removeNextLineStation);

        lineStations.remove(lastLineStation);

        return lastLineStation;
    }

    public long countOfStations() {
        return lineStations.size();
    }
}
