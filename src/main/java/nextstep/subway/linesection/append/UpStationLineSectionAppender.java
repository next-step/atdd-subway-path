package nextstep.subway.linesection.append;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.linesection.LineSection;
import nextstep.subway.linesection.LineSections;

import java.util.List;

public class UpStationLineSectionAppender implements LineSectionAppender {
    @Override
    public boolean append(LineSections sections, LineSection addSection) {
        int idx = sections.getIndex(addSection);
        if (sections.containsStation(addSection.getUpStation())) {
            LineSection section = sections.getSections().get(idx);
            checkDistance(section, addSection);
            sections.addToIndex(idx, LineSection.of(addSection.getLine(), section.getUpStation(), addSection.getDownStation(), addSection.getDistance()));
            sections.addToIndex(idx + 1, LineSection.of(addSection.getLine(), addSection.getDownStation(), section.getDownStation(), section.getDistance() - addSection.getDistance()));
            sections.removeSection(section);
            return true;
        }
        return false;
    }

    private void checkDistance(LineSection section, LineSection addSection) {
        if (section.getDistance() <= addSection.getDistance())
            throw new BadRequestException("request addSection distance must be smaller than exist lineSection disctance.");
    }
}
