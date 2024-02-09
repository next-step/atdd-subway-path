package nextstep.subway.application;

import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.repository.LineRepository;
import nextstep.subway.entity.repository.SectionRepository;
import nextstep.subway.entity.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    public SectionService(StationRepository stationRepository,
                          LineRepository lineRepository,
                          SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public SectionResponse createSection(SectionRequest request) {
        Line line = findLineById(request.getLineId());
        Section section = convertToSectionEntity(request, line);

        if (!existStation(section)) {
            throw new IllegalArgumentException("요청한 역은 존재하지 않습니다.");
        }
        
        line.addSection(section);
        return convertToResponse(section.setLine(line));
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationIdToDelete) {
        Line line = findLineById(lineId);

        if (!line.canSectionDelete(stationIdToDelete)) {
            throw new IllegalArgumentException("요청한 구간(혹은 역)을 삭제할 수 없습니다.");
        }
        line.deleteSection(findStation(stationIdToDelete));
    }

    private boolean existStation(Section section) {
        boolean upStationExists = stationRepository.findById(section.getUpStation().getId()).isPresent();
        boolean downStationExists = stationRepository.findById(section.getDownStation().getId()).isPresent();
        return upStationExists && downStationExists;
    }

    private Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(EntityNotFoundException::new);
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(IllegalArgumentException::new);
    }

    private Section convertToSectionEntity(SectionRequest request, Line line) {
        return new Section(
                findStation(request.getUpStationId()),
                findStation(request.getDownStationId()),
                request.getDistance(),
                line
        );
    }

    private SectionResponse convertToResponse(Section section) {
        return new SectionResponse(
                section.getId(),
                section.getUpStation().getId(),
                section.getDownStation().getId(),
                section.getDistance()
        );
    }
}