package nextstep.subway.line.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.exceptions.BadRequestException;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationsDto;
import nextstep.subway.station.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineResponse saveLine(LineRequest lineRequest, StationsDto stationsDto) {
        validateDuplicateLineName(lineRequest.getName());

        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Section section = createSection(line, stationsDto, lineRequest.getDistance());
        line.getSections().add(section);
        lineRepository.save(line);

        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .stations(Arrays.asList(
                        StationResponse.of(section.getUpStation()),
                        StationResponse.of(section.getDownStation())))
                .createdDate(line.getCreatedDate())
                .modifiedDate(line.getModifiedDate())
                .build();
    }

    private void validateDuplicateLineName(String name) {
        lineRepository.findByName(name).ifPresent(line -> {
            throw new BadRequestException("해당 노선은 이미 존재합니다.");
        });
    }

    private Section createSection(Line line, StationsDto stationsDto, int distance) {
        return Section.builder()
                .line(line)
                .upStation(stationsDto.getUpStation())
                .downStation(stationsDto.getDownStation())
                .distance(distance)
                .build();
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    private LineResponse createLineResponse(Line line) {
        Sections sections = new Sections(line.getSections());
        List<StationResponse> stations = sections.getStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .stations(stations)
                .createdDate(line.getCreatedDate())
                .modifiedDate(line.getModifiedDate())
                .build();
    }

    public LineResponse findLine(Long id) {
        Line line = findById(id);
        return createLineResponse(line);
    }

    public LineResponse updateLine(Long id, LineRequest request) {
        Line line = findById(id);
        line.update(request.getName(), request.getColor());
        return createLineResponse(line);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 노선입니다. lineId = " + id));
    }

    public SectionResponse addSection(Long lineId, StationsDto stationsDto, int distance) {
        Line line = findById(lineId);
        validateAddSection(new Sections(line.getSections()), stationsDto);

        Section section = Section.builder()
                .line(line)
                .upStation(stationsDto.getUpStation())
                .downStation(stationsDto.getDownStation())
                .distance(distance)
                .build();
        line.getSections().add(section);

        return SectionResponse.of(section);
    }

    private void validateAddSection(Sections sections, StationsDto stationsDto) {
        if(!sections.isDownStation(stationsDto.getUpStation())) {
            throw new BadRequestException("새로운 구간의 상행역은 현재 등록되어있는 하행 종점역이어야 합니다.");
        }

        if(sections.isRegisteredStation(stationsDto.getDownStation())) {
            throw new BadRequestException("새로운 구간의 하행역은 현재 등록되어있는 역일 수 없습니다.");
        }
    }

    public void deleteSection(Long lineId, Station station) {
        Line line = findById(lineId);

        Sections sections = new Sections(line.getSections());
        validateDeleteSection(sections, station);

        line.getSections().remove(sections.getLastSection());
    }

    private void validateDeleteSection(Sections sections, Station station) {
        if(!sections.canDelete()) {
            throw new BadRequestException("지하철 노선의 구간이 1개인 경우 구간을 삭제할 수 없습니다.");
        }

        if(!sections.isDownStation(station)) {
            throw new BadRequestException("지하철 노선에 등록된 마지막 역만 제거할 수 있습니다.");
        }
    }

}
