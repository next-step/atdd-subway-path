package nextstep.subway.linesection.append;

import nextstep.subway.linesection.LineSection;
import nextstep.subway.linesection.LineSections;

public class AppendInFirstLineSectionAppender implements LineSectionAppender {
    @Override
    public boolean append(LineSections sections, LineSection addSection) {
        if (isAddableInFirst(sections, addSection)) {
            sections.addInFirst(addSection);
            return true;
        }
        return false;
    }

    private boolean isAddableInFirst(LineSections sections, LineSection addSection) {
        return sections.getFirstStation().equals(addSection.getDownStation());
    }
}
