package nextstep.subway.linesection.append;

import nextstep.subway.linesection.LineSection;
import nextstep.subway.linesection.LineSections;

public class AppendInEndLineSectionAppender implements LineSectionAppender {

    @Override
    public boolean append(LineSections sections, LineSection addSection) {
        if (isAddableInEnd(sections,addSection)) {
            sections.addInEnd(addSection);
            return true;
        }
        return false;
    }

    private boolean isAddableInEnd(LineSections sections, LineSection addSection) {
        return sections.getLastStation().equals(addSection.getUpStation());
    }
}
