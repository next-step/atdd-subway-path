package atdd.path.service;

import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.EdgeRepository;
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
        return new LineResponseView();
    }
}
