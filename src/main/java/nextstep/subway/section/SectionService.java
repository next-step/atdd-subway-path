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

    public void addMiddleSection(Line line, Section requestSection, Section existingSection) {
        requestSection = sectionRepository.save(requestSection); //A-B 구간

        //기존 구간 역과 요청 구간 역으로 신규 구간 생성
        Section newSection = line.createNewSection(existingSection, requestSection); //B-C 구간
        newSection = sectionRepository.save(newSection);

        //요청 구간의 nextStationId에 신규 구간의 id로 셋팅
        requestSection.changeNextSection(newSection);

        //기존 구간의 이전 구간에 nextStationId 셋팅
        Section prevSection = sectionRepository.findByDownStation(requestSection.getUpStation()).orElse(null);
        if(prevSection != null) {
            prevSection.changeNextSection(requestSection);
        }

        //기존 구간 삭제
        line.removeSection(existingSection);

        //신규 구간 추가
        line.addMiddleSection(newSection);
        line.addMiddleSection(requestSection);
    }

    public void addSection(Line line, SectionRequest sectionRequest) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(
                () -> new BadRequestException("존재하지 않는 역입니다.")
        );
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(
                () -> new BadRequestException("존재하지 않는 역입니다.")
        );

        Section requestSection = new Section(upStation, downStation, sectionRequest.getDistance()); //A-B 구간
        Section existingSection = findExistingSection(requestSection); //A-C 구간
        if(existingSection != null) {
            addMiddleSection(line, requestSection, existingSection);
        }
        else {
            line.addEndSection(requestSection);
        }
    }

    public Section findExistingSection(Section requestSection) {
        Section existingSection = sectionRepository.findByUpStation(requestSection.getUpStation()).orElse(null);
        if(existingSection != null){
            requestSection.validMiddleSection(existingSection);
        }

        return existingSection;
    }
}
