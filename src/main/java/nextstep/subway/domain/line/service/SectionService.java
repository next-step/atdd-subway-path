package nextstep.subway.domain.line.service;

import nextstep.subway.domain.line.entity.Line;
import nextstep.subway.domain.line.entity.Section;
import nextstep.subway.infrastructure.line.dao.LineStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.domain.line.LineCommand;
import nextstep.subway.domain.line.LineInfo;
import nextstep.subway.infrastructure.line.dao.LineReader;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final LineStore lineStore;
    private final LineReader lineReader;

    public SectionService(LineStore lineStore, LineReader lineReader) {
        this.lineStore = lineStore;
        this.lineReader = lineReader;
    }

    @Transactional
    public LineInfo.Main saveSection(LineCommand.SectionAddCommand command) {
        Section section = lineStore.createSection(command);
        Line line = lineReader.readBy(command.getLineId());
        line.add(section);
        return LineInfo.Main.from(line);
    }

    @Transactional
    public LineInfo.Main removeSection(LineCommand.SectionDeleteCommand command) {
        Line line = lineReader.readBy(command.getLineId());
        line.remove(command.getStationId());
        return LineInfo.Main.from(line);
    }
}
