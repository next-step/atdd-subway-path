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
            validateSequence(request, line);
            validateUniqueness(request, line);
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
        validateLastStation(line);
        validateDownStationId(stationId, line);

        line.getSections().deleteLastSection();
    }

    private Line getLine(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Line Id '%d'를 찾을 수 없습니다.", lineId))
        );
    }

    // 라인의 섹션
    private void validateSequence(SectionCreateRequest request, Line line) {
        if (line.getSections().getDownStationId() != request.getUpStationId()) {
            throw new SectionMismatchException();
        }
    }

    private void validateUniqueness(SectionCreateRequest request, Line line) {
        if (line.getSections().getStationIds().contains(request.getDownStationId())) {
            throw new AlreadyRegisteredException();
        }
    }

    private void validateLastStation(Line line) {
        if (line.getSections().getStationIds().size() < 1) {
            throw new InsufficientStationException();
        }
    }

    private void validateDownStationId(Long stationId, Line line) {
        if (line.getSections().getDownStationId() != stationId) {
            throw new StationNotMatchException();
        }
    }

    private void validateStationId(Long stationId) {
        stationRepository.findById(stationId).orElseThrow(
                () -> new EntityNotFoundException("지하철역을 찾을 수 없습니다.")
        );
    }
}