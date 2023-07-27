package subway.db.h2.adapter.query;

import subway.db.h2.entity.SubwayLineJpa;
import subway.db.common.PersistenceAdapter;
import subway.db.h2.mapper.SubwayLineResponseMapper;
import subway.db.h2.repository.SubwayLineJpaRepository;
import subway.application.query.out.SubwayLineListQueryPort;
import subway.application.query.response.SubwayLineResponse;

import java.util.List;
import java.util.stream.Collectors;

@PersistenceAdapter
public class SubwayLineListQueryPersistenceAdapter implements SubwayLineListQueryPort {

    private final SubwayLineJpaRepository subwayLineJpaRepository;
    private final SubwayLineResponseMapper subwayLineResponseMapper;

    public SubwayLineListQueryPersistenceAdapter(SubwayLineJpaRepository subwayLineJpaRepository, SubwayLineResponseMapper subwayLineResponseMapper) {
        this.subwayLineJpaRepository = subwayLineJpaRepository;
        this.subwayLineResponseMapper = subwayLineResponseMapper;
    }

    @Override
    public List<SubwayLineResponse> findAll() {
        List<SubwayLineJpa> subwayLineJpas = subwayLineJpaRepository.findAllWithSections();
        return subwayLineJpas.stream().map(subwayLineResponseMapper::from).collect(Collectors.toList());
    }
}
