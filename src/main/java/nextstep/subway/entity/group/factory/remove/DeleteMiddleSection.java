package nextstep.subway.entity.group.factory.remove;

import java.util.List;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;

public class DeleteMiddleSection implements SectionDeleteAction {

    private final List<Section> removeSections;
    private final Line line;
    private final long deleteStationId;

    public DeleteMiddleSection(List<Section> removeSections, Line line, long deleteStationId) {
        this.removeSections = removeSections;
        this.line = line;
        this.deleteStationId = deleteStationId;
    }

    @Override
    public void action(List<Section> sections) {

        for (Section section : removeSections) {
            sections.remove(section);
        }

        sections.add(getSectionAfterDelete());
    }

    private Section getSectionAfterDelete() {

        Station upStation = removeSections.stream()
            .filter(section -> section.isEqualsDownStation(deleteStationId))
            .findFirst()
            .map(Section::getUpStation)
            .orElseThrow(
                () -> new IllegalArgumentException("삭제할 역의 상행역을 찾을 수 없습니다.")
            );

        Station downStation = removeSections.stream()
            .filter(section -> section.isEqualsUpStation(deleteStationId))
            .findFirst()
            .map(Section::getDownStation)
            .orElseThrow(
                () -> new IllegalArgumentException("삭제할 역의 하행역을 찾을 수 없습니다.")
            );

        return new Section(
            line,
            upStation,
            downStation,
            removeSections.stream().mapToInt(Section::getDistance).sum()
        );
    }
}
