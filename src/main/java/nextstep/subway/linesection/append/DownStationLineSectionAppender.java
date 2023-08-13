package nextstep.subway.linesection.append;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.linesection.LineSection;
import nextstep.subway.linesection.LineSections;
import org.springframework.stereotype.Component;

import java.util.List;

public class DownStationLineSectionAppender implements LineSectionAppender {
    @Override
    public void append(LineSections sections, LineSection addSection) {
        int idx = sections.getIndex(addSection);
        LineSection section = sections.getSectionByIndex(idx);
        checkDistance(section, addSection);
        sections.addToIndex(idx, addSection.getLine(), section.getUpStation(), addSection.getUpStation(), section.getDistance() - addSection.getDistance());
        sections.addToIndex(idx + 1, addSection.getLine(), addSection.getUpStation(), section.getDownStation(), addSection.getDistance());
        sections.removeSection(section);
    }

    @Override
    public boolean support(LineSections sections, LineSection addSection) {
        return sections.containsStation(addSection.getDownStation());
    }

    private void checkDistance(LineSection section, LineSection addSection) {
        if (section.getDistance() <= addSection.getDistance())
            throw new BadRequestException("request addSection distance must be smaller than exist lineSection disctance.");
    }
}
