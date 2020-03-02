package atdd.path.service;

import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.Optional;

@Service
public class LineService {
    private LineRepository lineRepository;
    private EdgeRepository edgeRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, EdgeRepository edgeRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.edgeRepository = edgeRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponseView create(LineRequestView requestView) {
        Optional<Line> lineByExactName = lineRepository.findByName(requestView.getName());
        if (lineByExactName.isPresent()) {
            throw new EntityExistsException("이미 등록된 노선입니다.");
        }

        if (requestView.getStartTime().isAfter(requestView.getEndTime())) {
            throw new IllegalArgumentException("노선의 시작시간은 종료시간보다 빨라야 합니다.");
        }

        if (requestView.getInterval() < 0) {
            throw new IllegalArgumentException("노선의 배차간격은 0보다 커야 합니다.");
        }

        Line line = lineRepository.save(requestView.toLine());
        return LineResponseView.of(line);
    }

    public void delete(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        if(line.isPresent()){
            lineRepository.deleteById(id);
        }
    }


}
