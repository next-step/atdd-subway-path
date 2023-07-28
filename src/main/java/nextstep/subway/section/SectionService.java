package nextstep.subway.section;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nextstep.subway.common.exception.ResourceNotFoundException;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    @PersistenceContext
    private final EntityManager em;

    public SectionService(final LineRepository lineRepository, final StationRepository stationRepository, final EntityManager em) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.em = em;
    }

    public SectionResponse createSection(final Long lineId, final SectionRequest request) {
        Line line = findLineById(lineId);
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        line.addSection(upStation, downStation, request.getDistance());
        em.flush();

        return new SectionResponse(line.getLastSection());
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        Line line = findLineById(lineId);

        line.deleteSection(findStationById(stationId));
    }

    private Line findLineById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Line.class, id));
    }

    private Station findStationById(final Long id) {
        return stationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Station.class, id));
    }

}
