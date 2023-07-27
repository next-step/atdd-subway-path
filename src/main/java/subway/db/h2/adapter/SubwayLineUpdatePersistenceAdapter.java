package subway.db.h2.adapter;

import subway.db.common.PersistenceAdapter;
import subway.db.h2.repository.SubwayLineJpaRepository;
import subway.db.h2.entity.SubwayLineJpa;
import subway.application.out.SubwayLineUpdatePort;
import subway.domain.SubwayLine;

import java.util.NoSuchElementException;

@PersistenceAdapter
class SubwayLineUpdatePersistenceAdapter implements SubwayLineUpdatePort {

    private final SubwayLineJpaRepository subwayLineJpaRepository;

    public SubwayLineUpdatePersistenceAdapter(SubwayLineJpaRepository subwayLineJpaRepository) {
        this.subwayLineJpaRepository = subwayLineJpaRepository;
    }
    @Override
    public void update(SubwayLine subwayLine) {
        SubwayLineJpa subwayLineJpa = subwayLineJpaRepository.findOneWithSectionsById(subwayLine.getId().getValue()).orElseThrow(() -> new NoSuchElementException("해당하는 지하철 노선이 없습니다."));
        subwayLineJpa.update(subwayLine.getName(), subwayLine.getColor());
        subwayLineJpaRepository.save(subwayLineJpa);
    }
}
