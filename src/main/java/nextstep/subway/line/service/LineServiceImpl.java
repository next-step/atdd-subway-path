package nextstep.subway.line.service;

import nextstep.subway.line.exception.LineNotExistException;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.repository.SectionRepository;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.service.dto.*;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.StationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineServiceImpl implements LineService, LineProvider {
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationProvider stationProvider;

    public LineServiceImpl(final LineRepository lineRepository, final SectionRepository sectionRepository, final StationProvider stationProvider) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationProvider = stationProvider;
    }

    @Override
    @Transactional
    public LineResponse saveLine(final LineCreateRequest lineCreateRequest) {
        lineCreateRequest.validate();

        final Line line = lineRepository.save(
                new Line(lineCreateRequest.getName()
                        , lineCreateRequest.getColor()
                        , createSection(lineCreateRequest.toSectionCreateRequest())
                ));

        return LineResponse.from(line);
    }

    @Override
    public List<LineResponse> findAllLines() {
        return lineRepository.findAllWithSections().stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public LineResponse findLineById(final Long id) {
        final Line line = findByIdWithSections(id);
        return LineResponse.from(line);
    }

    @Override
    @Transactional
    public void updateLine(final Long id, final LineUpdateRequest updateRequest) {
        updateRequest.validate();
        final Line line = lineRepository.findById(id).orElseThrow(() -> new LineNotExistException(id));
        line.changeName(updateRequest.getName());
        line.changeColor(updateRequest.getColor());
    }

    @Override
    @Transactional
    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    @Override
    @Transactional
    public SectionResponse addSection(final Long lineId, final SectionCreateRequest createRequest) {
        createRequest.validate();
        final Section savedSection = sectionRepository.save(createSection(createRequest));

        final Line line = findByIdWithSections(lineId);
        line.addSection(savedSection);

        return SectionResponse.from(savedSection);
    }

    @Override
    @Transactional
    public void removeSection(final Long lineId, final Long stationId) {
        final Station station = stationProvider.findById(stationId);
        final Line line = findByIdWithSections(lineId);
        line.removeSectionByStation(station);
    }

    private Section createSection(final SectionCreateRequest sectionCreateRequest) {
        final Station upStation = stationProvider.findById(sectionCreateRequest.getUpStationId());
        final Station downStation = stationProvider.findById(sectionCreateRequest.getDownStationId());
        return new Section(upStation, downStation, sectionCreateRequest.getDistance());
    }

    private Line findByIdWithSections(final Long id) {
        return lineRepository.findByIdWithSections(id).orElseThrow(() -> new LineNotExistException(id));
    }

    @Override
    public List<Line> getAllLines() {
        return lineRepository.findAllWithSections();
    }
}
