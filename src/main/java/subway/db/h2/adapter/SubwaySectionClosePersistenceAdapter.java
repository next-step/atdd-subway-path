package subway.db.h2.adapter;

import subway.db.common.PersistenceAdapter;
import subway.db.h2.entity.SubwayLineJpa;
import subway.db.h2.repository.SubwayLineJpaRepository;
import subway.application.command.out.SubwaySectionClosePort;
import subway.domain.SubwayLine;

import java.util.NoSuchElementException;

@PersistenceAdapter
public class SubwaySectionClosePersistenceAdapter implements SubwaySectionClosePort {

    private final SubwayLineJpaRepository subwayLineJpaRepository;

    public SubwaySectionClosePersistenceAdapter(SubwayLineJpaRepository subwayLineJpaRepository) {
        this.subwayLineJpaRepository = subwayLineJpaRepository;
    }


    @Override
    public void closeSection(SubwayLine subwayLine) {
        SubwayLineJpa subwayLineJpa = subwayLineJpaRepository.findOneWithSectionsById(subwayLine.getId().getValue()).orElseThrow(() -> new NoSuchElementException("해당하는 지하철 노선이 없습니다."));
        subwayLineJpa.deleteSections(subwayLine);
        subwayLineJpa.updateSections(subwayLine);
        subwayLineJpaRepository.save(subwayLineJpa);

    }
}
