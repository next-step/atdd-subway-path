package nextstep.subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRequest.createLine();
        line = lineRepository.save(line);

        return new LineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow();
        return new LineResponse(line);
    }

    @Transactional
    public void modifyLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow();
        line.changeLineInfo(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
