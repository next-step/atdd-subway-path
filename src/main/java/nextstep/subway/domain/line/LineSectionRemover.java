package nextstep.subway.domain.line;

import java.util.List;

import nextstep.subway.domain.line.exception.LineRemoveSectionException;
import nextstep.subway.domain.station.Station;

public class LineSectionRemover {
    private static final int MINIMUM_SECTION_SIZE = 1;

    public void remove(final LineSections lineSections, final Station station) {
        requireStationExist(lineSections.getStations(), station);
        requireNotLastSection(lineSections.getValue());

        final var sections = lineSections.getValue();

        final var upSection = lineSections.findSectionContainsAsDownStation(station);
        final var downSection = lineSections.findSectionContainsAsUpStation(station);

        if (upSection.isPresent() && downSection.isPresent()) {
            sections.add(concatSections(upSection.get(), downSection.get()));
        }

        upSection.ifPresent(sections::remove);
        downSection.ifPresent(sections::remove);
    }

    private void requireStationExist(final List<Station> stations, final Station station) {
        if (!stations.contains(station)) {
            throw new LineRemoveSectionException("노선의 해당 역이 존재하지 않습니다 : 역 id=" + station.getId());
        }
    }

    private void requireNotLastSection(final List<Section> sections) {
        if (sections.size() < MINIMUM_SECTION_SIZE) {
            throw new IllegalStateException("노선 내 구간 상태가 비정상적입니다");
        }
        if (sections.size() == MINIMUM_SECTION_SIZE) {
            throw new LineRemoveSectionException("상행 종점역과 하행 종점역만 있습니다");
        }
    }

    private Section concatSections(final Section upSection, final Section downSection) {
        return new Section(
                upSection.getUpStation(),
                downSection.getDownStation(),
                upSection.getDistance() + downSection.getDistance()
        );
    }
}
