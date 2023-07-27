package nextstep.subway.applicaion;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.applicaion.dto.*;
import nextstep.subway.applicaion.exception.domain.LineException;
import nextstep.subway.applicaion.exception.domain.SectionException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
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
        return createLineResponse(lineRepository.findById(id).orElseThrow(LineException::new));
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(LineException::new);
        line.setName(lineRequest.getName());
        line.setColor(lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineRepository
                .findById(lineId)
                .orElseThrow(LineException::new);

        if (addSectionByStationPoint(line, sectionRequest)) { return; }

        log.info("[LineService] Add section between saved section");
        for (Section savedSection : line.getSections()) {
            if (savedSection.getUpStation().getId() == sectionRequest.getUpStationId()) {
                beforeAddSection(line, savedSection, sectionRequest);
                // index switching
                /**
                 * 피드백 요청 코드 : 83 Line
                 * - A-B역이 있을 때 A을 기준으로 구간이 추가될 경우 A-C 역으로 저장
                 */
                line.addSectionAtIndex(new Section(line, upStation, downStation, sectionRequest.getDistance()),0);
                int calculatedDistance = savedSection.getDistance() - sectionRequest.getDistance();
                savedSection.updateSection(downStation, savedSection.getDownStation(), calculatedDistance);
                return;
            }
        }

        throw new SectionException("Requested sections`s stations is not saved");
    }

    @Transactional
    private boolean addSectionByStationPoint(Line line, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        int requestedDistance = sectionRequest.getDistance();

        List<Section> savedSections = line.getSections();
        Section startSection = savedSections.get(0);
        Station startStation = startSection.getUpStation();
        Station endStation = startSection.getDownStation();

        if (downStation.getId().equals(startStation.getId())) {
            log.info("[LineService] Add Section at starting point");
            startSection.updateSection(upStation, startStation, requestedDistance);
            line.getSections().add(new Section(line, downStation, endStation, startSection.getDistance()));
            return true;
        } else if (savedSections.get(savedSections.size() -1).getDownStation().getId().equals(upStation.getId())) {
            log.info("[LineService] Add section at end point");
            line.getSections().add(new Section(line, upStation, downStation, sectionRequest.getDistance()));
            return true;
        }
        return false;
    }

    private void beforeAddSection(Line line, Section savedSection, SectionRequest sectionRequest) {
        if (sectionRequest.getDistance() >= savedSection.getDistance()) {
            throw new SectionException("Requested distance size is equal or larger than the total saved size.");
        } else if (line.checkDuplicatedSectionByStationId(sectionRequest.getUpStationId(), sectionRequest.getDownStationId())) {
            throw new SectionException("Requested sections is duplicated");
        }
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(LineException::new);
        Station station = stationService.findById(stationId);

        if (!line.getSections().get(line.getSections().size() - 1).getDownStation().equals(station)) {
            throw new SectionException("Check requested stationId or lineId.");
        }

        line.getSections().remove(line.getSections().size() - 1);
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

    public List<StationResponse> createOrderedStationResponses(Line line) {
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
}