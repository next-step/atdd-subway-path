package nextstep.subway.linesection.append;

import nextstep.subway.linesection.LineSection;
import nextstep.subway.linesection.LineSections;
import org.springframework.stereotype.Component;

public class AppendInFirstLineSectionAppender implements LineSectionAppender {
    @Override
    public void append(LineSections sections, LineSection addSection) {
        sections.addInFirst(addSection);
    }

    @Override
    public boolean support(LineSections sections, LineSection addSection) {
        return isAddableInFirst(sections, addSection);
    }

    private boolean isAddableInFirst(LineSections sections, LineSection addSection) {
        return sections.getFirstStation().equals(addSection.getDownStation());
    }
}
