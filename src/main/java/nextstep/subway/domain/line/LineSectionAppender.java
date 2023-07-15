package nextstep.subway.domain.line;

import java.util.List;

import nextstep.subway.domain.line.exception.LineAppendSectionException;
import nextstep.subway.domain.station.Station;

public class LineSectionAppender {

    public void append(final LineSections lineSections, final Section section) {
        requireStationExistOneSideOnly(lineSections.getStations(), section);

        final var sections = lineSections.getValue();

        if (isAppendableInFront(lineSections, section)) {
            sections.add(section);
            return;
        }

        if (isAppendableBehind(lineSections, section)) {
            sections.add(section);
            return;
        }

        appendInTheMiddle(lineSections, section);
    }

    private void requireStationExistOneSideOnly(final List<Station> stations, final Section section) {
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new LineAppendSectionException(String.format(
                    "구간의 하행역과 상행역 모두 이미 노선에 존재합니다 : 구간의 상행역 id=%d, 구간의 하행역 id=%d",
                    section.getUpStation().getId(), section.getDownStation().getId()
            ));
        }

        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new LineAppendSectionException(String.format(
                    "구간의 하행역과 상행역 모두 노선에 존재하지 않습니다 : 구간의 상행역 id=%d, 구간의 하행역 id=%d",
                    section.getUpStation().getId(), section.getDownStation().getId()
            ));
        }
    }

    private boolean isAppendableInFront(final LineSections lineSections, final Section section) {
        final var lineFirstStation = lineSections.getFirstStation();
        final var sectionDownStation = section.getDownStation();

        return sectionDownStation.equalsId(lineFirstStation);
    }

    private boolean isAppendableBehind(final LineSections lineSections, final Section section) {
        final var lineLastStation = lineSections.getLastStation();
        final var sectionUpStation = section.getUpStation();

        return lineLastStation.equalsId(sectionUpStation);
    }

    private void appendInTheMiddle(final LineSections lineSections, final Section newSection) {
        final List<Section> sections = lineSections.getValue();

        if (existsUpStationIn(lineSections, newSection)) {
            final var originSection = lineSections.getValue().stream()
                    .filter(it -> it.getUpStation().equalsId(newSection.getUpStation()))
                    .findAny()
                    .orElseThrow(() -> new LineAppendSectionException("상행역이 포함된 노선 내 구간을 찾을 수 없습니다"));

            final var upStation = newSection.getDownStation();
            final var downStation = originSection.getDownStation();
            final var distance = subtractDistance(originSection.getDistance(), newSection.getDistance());
            final var separatedSection = new Section(upStation, downStation, distance);

            sections.remove(originSection);
            sections.add(newSection);
            sections.add(separatedSection);
            return;
        }

        if (existsDownStationIn(lineSections, newSection)) {
            final var originSection = lineSections.getValue().stream()
                    .filter(it -> it.getDownStation().equalsId(newSection.getDownStation()))
                    .findAny()
                    .orElseThrow(() -> new LineAppendSectionException("하행역이 포함된 노선 내 구간을 찾을 수 없습니다"));

            final var upStation = originSection.getUpStation();
            final var downStation = newSection.getUpStation();
            final var distance = subtractDistance(originSection.getDistance(), newSection.getDistance());
            final var separatedSection = new Section(upStation, downStation, distance);

            sections.remove(originSection);
            sections.add(newSection);
            sections.add(separatedSection);
        }
    }

    private boolean existsUpStationIn(final LineSections lineSections, final Section section) {
        final List<Station> stations = lineSections.getStations();
        return stations.contains(section.getUpStation());
    }

    private boolean existsDownStationIn(final LineSections lineSections, final Section section) {
        final List<Station> stations = lineSections.getStations();
        return stations.contains(section.getDownStation());
    }

    private int subtractDistance(final int origin, final int other) {
        final int distance = origin - other;
        if (distance <= 0) {
            throw new LineAppendSectionException("기존 역 사이 길이보다 크거나 같습니다");
        }
        return distance;
    }
}
