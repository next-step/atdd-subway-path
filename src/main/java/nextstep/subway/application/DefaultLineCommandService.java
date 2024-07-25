package nextstep.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import nextstep.subway.domain.model.Station;
import nextstep.subway.domain.repository.StationRepository;
import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.service.LineCommandService;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.domain.model.Section;
import nextstep.subway.application.dto.LineRequest;
import nextstep.subway.application.dto.SectionRequest;
import nextstep.subway.application.dto.SectionResponse;
import nextstep.subway.domain.service.SectionAdditionStrategy;
import nextstep.subway.domain.service.SectionAdditionStrategyFactory;

@Service
@Transactional
public class DefaultLineCommandService implements LineCommandService {
    public static final String LINE_NOT_FOUND_MESSAGE = "노선을 찾을 수 없습니다.";
    public static final String STATION_NOT_FOUND_MESSAGE = "역을 찾을 수 없습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionAdditionStrategyFactory sectionAdditionStrategyFactory;


    public DefaultLineCommandService(
        LineRepository lineRepository,
        StationRepository stationRepository,
        SectionAdditionStrategyFactory sectionAdditionStrategyFactory
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionAdditionStrategyFactory = sectionAdditionStrategyFactory;
    }

    @Override
    public LineResponse saveLine(LineRequest lineRequest) {
        final Station upStation = findStationOrElseThrow(lineRequest.getUpStationId());
        final Station downStation = findStationOrElseThrow(lineRequest.getDownStationId());
        final Section section = Section.builder()
            .upStation(upStation)
            .downStation(downStation)
            .distance(lineRequest.getDistance())
            .build();

        Line line = new Line(
            lineRequest.getName(),
            lineRequest.getColor()
        );

        line.addSection(section);

        Line savedLine = lineRepository.save(line);
        return LineResponse.from(savedLine);
    }

    @Override
    public void updateLine(Long id, LineRequest lineRequest) {
        lineRepository.save(findLineOrElseThrow(id).getUpdated(lineRequest.getName(), lineRequest.getColor()));
    }

    @Override
    public void deleteLineById(Long id) {
        if (!lineRepository.existsById(id)) {
            throw new IllegalArgumentException(LINE_NOT_FOUND_MESSAGE);
        }

        lineRepository.deleteById(id);
    }

    @Override
    public SectionResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineOrElseThrow(lineId);
        Station upStation = findStationOrElseThrow(sectionRequest.getUpStationId());
        Station downStation = findStationOrElseThrow(sectionRequest.getDownStationId());

        Section section = Section.builder()
            .line(line)
            .upStation(upStation)
            .downStation(downStation)
            .distance(sectionRequest.getDistance())
            .build();

        SectionAdditionStrategy strategy = sectionAdditionStrategyFactory.getStrategy(line, section);
        line.addSection(strategy, section);

        lineRepository.save(line);
        return SectionResponse.from(section);
    }

    @Override
    public void removeSection(Long lineId, Long stationId) {
        Line line = findLineOrElseThrow(lineId);
        Station station = findStationOrElseThrow(stationId);

        line.removeSection(station);
        lineRepository.save(line);
    }

    private Line findLineOrElseThrow(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException(LINE_NOT_FOUND_MESSAGE));
    }

    private Station findStationOrElseThrow(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(() -> new IllegalArgumentException(STATION_NOT_FOUND_MESSAGE));
    }
}
