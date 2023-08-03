package subway.db.h2.adapter;

import subway.db.common.PersistenceAdapter;
import subway.db.h2.mapper.SubwayLineJpaMapper;
import subway.db.h2.mapper.SubwayLineResponseMapper;
import subway.db.h2.repository.SubwayLineJpaRepository;
import subway.db.h2.entity.SubwayLineJpa;
import subway.application.command.out.SubwayLineRegisterPort;
import subway.application.response.SubwayLineResponse;
import subway.domain.SubwayLine;

@PersistenceAdapter
class SubwayLineRegisterPersistenceAdapter implements SubwayLineRegisterPort {

    private final SubwayLineJpaRepository subwayLineJpaRepository;
    private final SubwayLineResponseMapper subwayLineResponseMapper;
    private final SubwayLineJpaMapper subwayLineJpaMapper;

    public SubwayLineRegisterPersistenceAdapter(SubwayLineJpaRepository subwayLineJpaRepository, SubwayLineResponseMapper subwayLineResponseMapper, SubwayLineJpaMapper subwayLineJpaMapper) {
        this.subwayLineJpaRepository = subwayLineJpaRepository;
        this.subwayLineResponseMapper = subwayLineResponseMapper;
        this.subwayLineJpaMapper = subwayLineJpaMapper;
    }

    @Override
    public SubwayLineResponse register(SubwayLine subwayLine) {
        SubwayLineJpa subwayLineJpa = subwayLineJpaMapper.from(subwayLine);

        subwayLineJpaRepository.save(subwayLineJpa);
        return subwayLineResponseMapper.from(subwayLineJpa);
    }
}
