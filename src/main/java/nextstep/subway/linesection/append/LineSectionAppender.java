package nextstep.subway.linesection.append;

import nextstep.subway.linesection.LineSection;
import nextstep.subway.linesection.LineSections;

public interface LineSectionAppender {
    /**
     * @param sections
     * @param addSection
     * @return
     */
    void append(LineSections sections, LineSection addSection);

    boolean support(LineSections sections, LineSection addSection);
}
