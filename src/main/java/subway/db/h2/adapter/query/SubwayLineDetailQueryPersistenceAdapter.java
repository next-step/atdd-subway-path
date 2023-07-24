package subway.db.h2.adapter.query;

import subway.core.common.PersistenceAdapter;
import subway.db.h2.entity.SubwayLineJpa;
import subway.db.h2.mapper.SubwayLineResponseMapper;
import subway.db.h2.repository.SubwayLineJpaRepository;
import subway.application.query.out.SubwayLineDetailQueryPort;
import subway.application.query.response.SubwayLineResponse;
import subway.domain.SubwayLine;

import java.util.NoSuchElementException;

@PersistenceAdapter
public class SubwayLineDetailQueryPersistenceAdapter implements SubwayLineDetailQueryPort {

    private final SubwayLineJpaRepository subwayLineJpaRepository;
    private final SubwayLineResponseMapper subwayLineResponseMapper;

    public SubwayLineDetailQueryPersistenceAdapter(SubwayLineJpaRepository subwayLineJpaRepository, SubwayLineResponseMapper subwayLineResponseMapper) {
        this.subwayLineJpaRepository = subwayLineJpaRepository;
        this.subwayLineResponseMapper = subwayLineResponseMapper;
    }

    @Override
    public SubwayLineResponse findOne(SubwayLine.Id id) {
        SubwayLineJpa subwayLineJpa = subwayLineJpaRepository.findById(id.getValue())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 지하철 노선입니다."));
        return subwayLineResponseMapper.from(subwayLineJpa);
    }
}
