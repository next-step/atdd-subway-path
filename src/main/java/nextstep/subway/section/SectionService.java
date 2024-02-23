package nextstep.subway.section;

import nextstep.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class SectionService {
    private final SectionRepository sectionRepository;

    private final StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    private Section validSection(Line line, SectionRequest sectionRequest) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(
                () -> new BadRequestException("존재하지 않는 역입니다.")
        );
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(
                () -> new BadRequestException("존재하지 않는 역입니다.")
        );

        Section requestSection = new Section(upStation, downStation, sectionRequest.getDistance(), line);

        return requestSection;
    }

    public void addSection(Line line, SectionRequest sectionRequest) {
        Section requestSection = validSection(line, sectionRequest);
        line.addSection(requestSection);
    }

    public void deleteSection(Line line, Station deleteStation) {
        line.deleteSection(deleteStation);
    }

}
