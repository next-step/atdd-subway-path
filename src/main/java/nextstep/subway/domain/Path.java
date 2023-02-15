package nextstep.subway.domain;

import nextstep.subway.common.exception.NoRegisterStationException;
import nextstep.subway.common.exception.SameStationException;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static nextstep.subway.common.error.SubwayError.*;

public class Path {

    private List<Station> stations;
    private List<Section> sections;
    private Station sourceStation;
    private Station targetStation;

    public Path(final List<Station> stations, final List<Section> sections, final Station sourceStation, final Station targetStation) {
        validate(stations, sourceStation, targetStation);
        this.stations = stations;
        this.sections = sections;
        this.sourceStation = sourceStation;
        this.targetStation = targetStation;
    }

    public static Path of(final List<Line> lines, final Station sourceStation, final Station targetStation) {
        return new Path(findAllStations(lines), findAllSections(lines), sourceStation, targetStation);
    }

    private void validate(final List<Station> stations, final Station sourceStation, final Station targetStation) {
        validateSameStation(sourceStation, targetStation);
        validateNoRegister(stations, sourceStation, targetStation);
    }

    private void validateNoRegister(final List<Station> stations, final Station sourceStation, final Station targetStation) {
        if (!stations.contains(sourceStation) || !stations.contains(targetStation)) {
            throw new NoRegisterStationException(NO_REGISTER_LINE_STATION);
        }
    }

    private void validateSameStation(final Station sourceStation, final Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new SameStationException(NO_FIND_SAME_SOURCE_TARGET_STATION);
        }
    }

    private static List<Station> findAllStations(final List<Line> lines) {
        return lines.stream()
                .map(Line::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private static List<Section> findAllSections(final List<Line> lines) {
        return lines.stream()
                .map(Line::getSectionList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Station> getStations() {
        return stations;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Station getSourceStation() {
        return sourceStation;
    }

    public Station getTargetStation() {
        return targetStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(stations, path.stations) && Objects.equals(sections, path.sections) && Objects.equals(sourceStation, path.sourceStation) && Objects.equals(targetStation, path.targetStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations, sections, sourceStation, targetStation);
    }
}
