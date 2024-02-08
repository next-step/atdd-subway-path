package nextstep.subway.domain.line.service;

import nextstep.subway.infrastructure.line.dao.LineStore;
import nextstep.subway.interfaces.line.dto.LineRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.domain.line.LineInfo;
import nextstep.subway.domain.line.entity.Line;
import nextstep.subway.domain.line.entity.Section;
import nextstep.subway.infrastructure.line.dao.LineReader;
import nextstep.subway.interfaces.line.dto.LinePatchRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineStore lineStore;
    private final LineReader lineReader;

    public LineService(LineStore lineStore, LineReader lineReader) {
        this.lineStore = lineStore;
        this.lineReader = lineReader;
    }

    @Transactional
    public LineInfo.Main saveLine(LineRequest.Line request) {
        Section section = lineStore.createSection(request);
        Line init = new Line(request.getName(), request.getColor(), section);
        return LineInfo.Main.from(lineStore.store(init));
    }

    @Transactional(readOnly = true)
    public LineInfo.Main retrieveBy(Long id) {
        Line line = lineReader.readBy(id);
        return LineInfo.Main.from(line);
    }

    @Transactional(readOnly = true)
    public List<LineInfo.Main> listAll() {
        return lineReader.listAll().stream().map(LineInfo.Main::from).collect(Collectors.toList());
    }

    @Transactional
    public void updateBy(Long id, LinePatchRequest request) {
        Line line = lineReader.readBy(id);
        line.changeName(request.getName());
        line.changeColor(request.getColor());
    }

    @Transactional
    public void deleteBy(Long id) {
        Line line = lineReader.readBy(id);
        lineStore.remove(line);
    }
}
