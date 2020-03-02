package atdd.path.service;

import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.EdgeRepository;
import atdd.path.domain.Line;
import atdd.path.domain.LineRepository;
import org.springframework.stereotype.Service;

@Service
public class LineService {
    private LineRepository lineRepository;
    private EdgeRepository edgeRepository;

    public LineService(LineRepository lineRepository, EdgeRepository edgeRepository) {
        this.lineRepository = lineRepository;
        this.edgeRepository = edgeRepository;
    }

    public LineResponseView create(LineRequestView requestView){
        if(requestView.getStartTime().isAfter(requestView.getEndTime())){
            throw new IllegalArgumentException("노선의 시작시간은 종료시간보다 빨라야 합니다.");
        }
        if(requestView.getInterval() < 0){
            throw new IllegalArgumentException("노선의 배차간격은 0보다 커야 합니다.");
        }
        Line line = lineRepository.save(requestView.toLine());
        return LineResponseView.of(line);
    }
}
