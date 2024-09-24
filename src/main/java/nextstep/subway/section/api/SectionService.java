package nextstep.subway.section.api;

import lombok.RequiredArgsConstructor;
import nextstep.subway.global.exception.AlreadyRegisteredException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.path.domain.PathEvent;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.section.api.response.SectionResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.presentation.request.SectionCreateRequest;
import nextstep.subway.station.StationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public SectionResponse create(Long lineId, SectionCreateRequest request, PathEvent event) {
        eventPublisher.publishEvent(event);

        Line line = getLine(lineId);

        if (line.getSections().getStations().contains(request.getUpStationId()) && line.getSections().getStations().contains(request.getDownStationId())) {
            throw new AlreadyRegisteredException();
        }

        if (line.getSections().getStations().contains(request.getUpStationId())) {
            // 새로 추가하는 역은 request.다운스테이션
            return SectionResponse.of(createByDownstationId(request, line));
        }

        // 새로운 역은 request.getUpstationId
         return SectionResponse.of(createByUpstationId(request, line));
    }

    @Transactional
    public void delete(Long lineId, Long stationId, PathEvent event) {
        eventPublisher.publishEvent(event);

        Line line = getLine(lineId);

        // 찾는 역이 존재하지 않으면 예외
        validateStationId(stationId);

        Section sectionGetByUpstation = line.getSections().getSections().stream().filter(
                section -> section.getUpStationId() == stationId
        ).findFirst().orElse(null);

        Section sectionGetByDownStation = line.getSections().getSections().stream().filter(
                section -> section.getDownStationId() == stationId
        ).findFirst().orElse(null);

        // 섹션 제거 및 수정
        if (sectionGetByUpstation != null && sectionGetByDownStation == null) {
            line.getSections().removeSection(sectionGetByUpstation);
        } else if (sectionGetByUpstation != null && sectionGetByDownStation != null) {
            sectionGetByDownStation.changeDownStationId(sectionGetByUpstation.getDownStationId());
            sectionGetByDownStation.changeDistance(sectionGetByDownStation.getDistance() + sectionGetByUpstation.getDistance());
            line.getSections().removeSection(sectionGetByUpstation);
        } else if (sectionGetByUpstation == null && sectionGetByDownStation != null) {
            line.getSections().removeSection(sectionGetByDownStation);
        }
    }

    private Section createByDownstationId(SectionCreateRequest request, Line line) {

        int index = line.getSections().getStations().indexOf(request.getUpStationId());

        if (index == line.getSections().getStations().size() - 1) {
            // 마지막에 추가하는 경우
            Section newSection = SectionCreateRequest.toEntity(
                    request.getUpStationId(),
                    request.getDownStationId(),
                    request.getDistance()
            );
            sectionRepository.save(newSection);
            line.getSections().addSection(newSection);
            return newSection;
        }

        // 중간에 추가
        Long originalUpStationId = line.getSections().getStations().get(index);

        // 기존 구간 찾기
        Section originalSection = line.getSections().getSections().stream().filter(
                section -> section.getUpStationId() == originalUpStationId
        ).findFirst().orElseThrow(
                () -> new EntityNotFoundException("구간이 존재하지 않습니다.")
        );

        // 기존 구간에 새로운 구간 추가
        Section newSection = SectionCreateRequest.toEntity(
                request.getDownStationId(),
                originalSection.getDownStationId(),
                originalSection.getDistance() - request.getDistance()
        );
        int newIndex = nextIndexOfOriginalSection(line, originalSection);
        line.getSections().getSections().add(newIndex, newSection);

        // 기존 구간 정보 변경
        originalSection.changeDownStationId(request.getDownStationId());
        originalSection.changeDistance(request.getDistance());

        return newSection;
    }

    private Section createByUpstationId(SectionCreateRequest request, Line line) {
        int index = line.getSections().getStations().indexOf(request.getDownStationId());

        if (index == 0) {
            // 라인의 첫구간에 추가
            Section newSection = SectionCreateRequest.toEntity(
                    request.getUpStationId(),
                    request.getDownStationId(),
                    request.getDistance()
            );

            line.getSections().getSections().add(0, newSection);
            return newSection;
        }

        // 라인의 중간에 추가
        Long originalDownStationId = line.getSections().getStations().get(index);

        Section originalSection = line.getSections().getSections().stream().filter(
                section -> section.getDownStationId() == originalDownStationId
        ).findFirst().orElseThrow(
                () -> new EntityNotFoundException("구간이 존재하지 않습니다.")
        );

        Section newSection = SectionCreateRequest.toEntity(
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()
        );

        int newIndex = nextIndexOfOriginalSection(line, originalSection);
        line.getSections().getSections().add(newIndex, newSection);

        // 기존 구간 정보 변경
        originalSection.changeDownStationId(request.getUpStationId());
        originalSection.changeDistance(originalSection.getDistance() - request.getDistance());

        return newSection;
    }

    private int nextIndexOfOriginalSection(Line line, Section originalSection) {
        return line.getSections().getSections().indexOf(originalSection) + 1;
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