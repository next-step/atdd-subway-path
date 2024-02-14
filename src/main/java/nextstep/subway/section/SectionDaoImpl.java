package nextstep.subway.section;

import org.springframework.stereotype.Component;


@Component
public class SectionDaoImpl implements SectionDao {
    private final SectionRepository sectionRepository;

    public SectionDaoImpl(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    public Section save(Section section) {
        return sectionRepository.save(section);
    }

    @Override
    public Section findUpStation(long upStationsId) {
        return sectionRepository.findByUpStationId(upStationsId);
    }

    @Override
    public Section findDownStation(long downStationsId) {
        return sectionRepository.findByDownStationId(downStationsId);
    }

    @Override
    public LineSections findSectionsByLine(Long lineId) {
        return new LineSections(sectionRepository.findAllByLineId(lineId));
    }

    @Override
    public void deleteSection(Section section) {
        sectionRepository.delete(section);
    }

}
