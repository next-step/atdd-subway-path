package nextstep.subway.linesection.remove;

import nextstep.subway.linesection.LineSection;
import nextstep.subway.linesection.LineSections;
import nextstep.subway.station.Station;

public class MiddleStationRemover implements LineSectionRemover {
    @Override
    public boolean remove(LineSections sections, Station deleteStation) {
        LineSection beforeSection = sections.findSectionByCondition(section -> section.getDownStation().equals(deleteStation));
        LineSection nextSection = sections.findSectionByCondition(section -> section.getUpStation().equals(deleteStation));
        int idx = sections.getIndex(beforeSection);
        sections.removeSection(beforeSection);
        sections.removeSection(nextSection);
        sections.addToIndex(idx, LineSection.of(beforeSection.getLine(), beforeSection.getUpStation(), nextSection.getDownStation(), beforeSection.getDistance() + nextSection.getDistance()));
        return true;
    }
}
