package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(
                new Line(request.getName(), request.getColor(), request.getUpStationId(), request.getDownStationId())
        );
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        line.getSections().add(new Section(line, upStation, downStation, request.getDistance()));

        return createLineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return createLineResponse(lineRepository.findById(id).orElseThrow(IllegalArgumentException::new));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        if (lineRequest.getName() != null) {
            line.setName(lineRequest.getName());
        }
        if (lineRequest.getColor() != null) {
            line.setColor(lineRequest.getColor());
        }
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);

        validateAddSectionConditions(line, sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
        updateSectionInformation(line, upStation, downStation, sectionRequest.getDistance());

        line.addSection(new Section(line, upStation, downStation, sectionRequest.getDistance()));
    }

    private void updateSectionInformation(Line line,
                                          Station newUpStation,
                                          Station newDownStation,
                                          int newSectionDistance) {
        List<Section> alreadyExistSections = line.getSections();
        for (Section section : alreadyExistSections) {

            //기존 구간의 상행역 == 새로 추가하는 구간의 상행역
            if (section.getUpStationId() == newUpStation.getId()) {
                if (section.getDistance() <= newSectionDistance) {
                    throw new IllegalArgumentException();
                }
                section.updateUpStation(newDownStation);
                section.updateDistance(section.getDistance() - newSectionDistance);
                break;
            }

            //기존 구간의 하행역 == 새로 추가하는 구간의 하행역
            if (section.getDownStationId() == newDownStation.getId()) {
                if (section.getDistance() <= newSectionDistance) {
                    throw new IllegalArgumentException();
                }
                section.updateDownStation(newUpStation);
                section.updateDistance(section.getDistance() - newSectionDistance);
                break;
            }

            //기존 구간의 상행 종착역 == 새로 추가하는 구간의 하행역
            if ((section.getUpStationId() == newDownStation.getId())
                    && (section.getUpStationId() == line.getFinalUpStationId())) {
                if (section.getDistance() <= newSectionDistance) {
                    throw new IllegalArgumentException();
                }
                line.updateFinalUpStationId(newUpStation.getId());
                break;
            }

            //기존 구간의 하행 종착역 == 새로 추가하는 구간의 상행역
            if ((section.getDownStationId() == newUpStation.getId())
                    && (section.getDownStationId() == line.getFinalDownStationId())) {
                if (section.getDistance() <= newSectionDistance) {
                    throw new IllegalArgumentException();
                }
                line.updateFinalDownStationId(newDownStation.getId());
                break;
            }
        }
    }

    private void validateAddSectionConditions(Line line, Long newUpStationId, Long newDownStationId) {
        List<Section> sections = line.getSections();
        Set<Long> stationIds = getSectionContainStationsSet(sections);

        if (stationIds.contains(newUpStationId) && stationIds.contains(newDownStationId)) {
            throw new IllegalArgumentException();
        }

        if (!stationIds.contains(newUpStationId) && !stationIds.contains(newDownStationId)) {
            throw new IllegalArgumentException();
        }
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                createStationResponses(line)
        );
    }

    private List<StationResponse> createStationResponses(Line line) {
        if (line.getSections().isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = line.getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, line.getSections().get(0).getUpStation());

        return stations.stream()
                .map(it -> stationService.createStationResponse(it))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        Station station = stationService.findById(stationId);

        validateSectionDeleteRequest(line, station);

        List<Section> sections = line.getSections();
        List<Section> filteredSections = sections.stream()
                .filter(
                        s -> (s.getUpStationId() == stationId) || (s.getDownStationId() == stationId)
                ).collect(Collectors.toList());

        //중간에 위치한 경우
        if(filteredSections.size() > 1) {
            Section first, second;

            if(filteredSections.get(0).getUpStationId() == stationId) {
                first = filteredSections.get(1);
                second = filteredSections.get(0);
            } else {
                first = filteredSections.get(0);
                second = filteredSections.get(1);
            }

            first.updateDownStation(second.getDownStation());
            first.updateDistance(first.getDistance() + second.getDistance());

            line.deleteSection(second);

            return;
        }

        //상행 종착 구간에 위치한 경우
        if (filteredSections.get(0).getUpStationId() == stationId) {
            line.updateFinalUpStationId(filteredSections.get(0).getDownStationId());
        } else {
            // 하행 종착 구간에 위치한 경우
            line.updateFinalDownStationId(filteredSections.get(0).getUpStationId());
        }

        line.deleteSection(filteredSections.get(0));
    }

    private void validateSectionDeleteRequest(Line line, Station targetStation) {
        List<Section> sections = line.getSections();
        if (sections.size() <= 1) {
            throw new UnsupportedOperationException();
        }

        Set<Long> sectionContainStationsSet = getSectionContainStationsSet(sections);
        if (!sectionContainStationsSet.contains(targetStation.getId())) {
            throw new IllegalArgumentException();
        }
    }

    Set<Long> getSectionContainStationsSet(List<Section> sections) {
        List<Long> sectionUpStationIds = sections.stream()
                .map(s -> s.getUpStationId())
                .collect(Collectors.toList());
        List<Long> sectionDownStationIds = sections.stream()
                .map(s -> s.getDownStationId())
                .collect(Collectors.toList());

        return Stream.of(sectionUpStationIds, sectionDownStationIds)
                .flatMap(x -> x.stream())
                .collect(Collectors.toSet());
    }
}
