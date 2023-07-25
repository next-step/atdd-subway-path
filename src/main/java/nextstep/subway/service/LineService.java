package nextstep.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.entity.Line;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.service.request.LineRequest;

@Service
@Transactional
public class LineService {

    private final LineRepository repository;

    public LineService(LineRepository repository) {
        this.repository = repository;
    }

    public Line create(LineRequest request) {

        return repository.save(
            new Line(
                request.getName(),
                request.getColor()
            )
        );
    }

    public List<Line> findAllLine() {

        return repository.findAll();
    }

    public Line findById(long id) {
        return repository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("해당하는 id 에 맞는 지하철 노선이 존재하지 않습니다.")
        );
    }

    public void modify(long id, String name, String color) {

        Line line = findById(id);
        line.updateName(name);
        line.updateColor(color);

        repository.save(line);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    public void deleteSectionStation(long lineId, long stationId) {

        final Line line = findById(lineId);
        line.removeSection(stationId);
    }
}
