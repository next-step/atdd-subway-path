package nextstep.subway.linesection.append;

import nextstep.subway.linesection.LineSection;
import nextstep.subway.linesection.LineSections;
import org.springframework.stereotype.Component;

public class AppendInEndLineSectionAppender implements LineSectionAppender {

    @Override
    public void append(LineSections sections, LineSection addSection) {
        sections.addInEnd(addSection);
    }

    @Override
    public boolean support(LineSections sections, LineSection addSection) {
        return isAddableInEnd(sections, addSection);
    }

    private boolean isAddableInEnd(LineSections sections, LineSection addSection) {
        return sections.getLastStation().equals(addSection.getUpStation());
    }
}
