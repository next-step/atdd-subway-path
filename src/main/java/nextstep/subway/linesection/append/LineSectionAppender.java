package nextstep.subway.linesection.append;

import nextstep.subway.linesection.LineSection;
import nextstep.subway.linesection.LineSections;

public interface LineSectionAppender {
    /**
     * @param sections
     * @param addSection
     * @return 실행시 1, 실행 안했을 시 0
     */
    boolean append(LineSections sections, LineSection addSection);
}
