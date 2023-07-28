package nextstep.subway.service;

import java.util.List;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
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

        return save(Section.of(line, upStation, downStation, distance));
    }

    public Section save(Section section){
        return sectionRepository.save(section);
    }

    public List<Section> findAllByLineId(Long lineId) {

        return sectionRepository.findAllByLineId(lineId)
            .orElseThrow(
                () -> new IllegalArgumentException(lineId + " id 값을 가지는 노선 구간이 존재하지 않습니다.")
            );

    }

    public List<Section> findAll() {

        return sectionRepository.findAll();
    }
}
