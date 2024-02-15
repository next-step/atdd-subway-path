package nextstep.subway.section.api;

import nextstep.subway.global.exception.AlreadyRegisteredException;
import nextstep.subway.global.exception.InsufficientStationException;
import nextstep.subway.global.exception.SectionMismatchException;
import nextstep.subway.global.exception.StationNotMatchException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.section.api.response.SectionResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.presentation.request.SectionCreateRequest;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, LineRepository lineRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public SectionResponse create(Long lineId, SectionCreateRequest request) {
        Line line = getLine(lineId);

        if (line.getSections() != null) {
            line.validateSequence(request);
            line.validateUniqueness(request);
        }

        Section section = SectionCreateRequest.toEntity(
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()
        );
        Section savedSection = sectionRepository.save(section);
        line.getSections().addSection(savedSection);
        return SectionResponse.of(savedSection);
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Line line = getLine(lineId);

        validateStationId(stationId);
        line.validateLastStation();
        line.validateDownStationId(stationId);

        line.getSections().deleteLastSection();
    }

    private Line getLine(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Line Id '%d'를 찾을 수 없습니다.", lineId))
        );
    }

    private void validateStationId(Long stationId) {
        stationRepository.findById(stationId).orElseThrow(
                () -> new EntityNotFoundException("지하철역을 찾을 수 없습니다.")
        );
    }
}