package nextstep.subway.linesection;

import nextstep.subway.station.Station;

public class AppendInEndLineSectionAppender implements LineSectionAppender {

    @Override
    public boolean append(LineSections sections, LineSection addSection) {
        if (isAddableInEnd(sections,addSection)) {
            sections.getSections().add(addSection);
            return true;
        }
        return false;
    }

    private boolean isAddableInEnd(LineSections sections, LineSection addSection) {
        return sections.getLastStation().equals(addSection.getUpStation());
    }
}
