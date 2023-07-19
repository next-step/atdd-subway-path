package nextstep.subway.service;

import java.util.List;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.group.SectionGroup;
import nextstep.subway.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Section create(Line line,Station upStation, Station downStation,
        int distance) {

        return save(Section.firstCreate(line, upStation, downStation, distance));
    }

    private Section save(Section section){
        return sectionRepository.save(section);
    }

    public void add(Line line, Station upStation, Station downStation, int distance) {

        SectionGroup group = line.getSections();
        Section section = group.add(line, upStation, downStation, distance);

        sectionRepository.save(section);
    }

    public List<Section> findAllByLineId(Long lineId) {

        return sectionRepository.findAllByLineId(lineId)
            .orElseThrow(
                () -> new IllegalArgumentException(lineId + " id 값을 가지는 노선 구간이 존재하지 않습니다.")
            );

    }
}
