package subway.db.h2.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import subway.db.common.PersistenceAdapter;
import subway.db.h2.mapper.SubwayLineMapper;
import subway.db.h2.repository.SubwayLineJpaRepository;
import subway.db.h2.entity.SubwayLineJpa;
import subway.application.command.out.SubwayLineLoadPort;
import subway.domain.SubwayLine;

import java.util.NoSuchElementException;
import java.util.Optional;

@PersistenceAdapter
public class SubwayLineLoadPersistenceAdapter implements SubwayLineLoadPort {

    private final SubwayLineJpaRepository subwayLineJpaRepository;
    private final SubwayLineMapper subwayLineMapper;

    @Autowired
    public SubwayLineLoadPersistenceAdapter(SubwayLineJpaRepository subwayLineJpaRepository, SubwayLineMapper subwayLineMapper) {
        this.subwayLineJpaRepository = subwayLineJpaRepository;
        this.subwayLineMapper = subwayLineMapper;
    }

    @Override
    public Optional<SubwayLine> findOne(SubwayLine.Id id) {
        SubwayLineJpa subwayLineJpa = subwayLineJpaRepository.findOneWithSectionsById(id.getValue()).orElseThrow(() -> new NoSuchElementException("해당하는 지하철 노선이 없습니다."));
        return Optional.of(subwayLineMapper.from(subwayLineJpa));
    }
}
