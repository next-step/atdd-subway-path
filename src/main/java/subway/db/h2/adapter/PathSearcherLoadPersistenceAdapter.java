package subway.db.h2.adapter;

import lombok.RequiredArgsConstructor;
import subway.application.query.out.PathSearcherLoadPort;
import subway.db.common.PersistenceAdapter;
import subway.db.h2.entity.SubwaySectionJpa;
import subway.db.h2.mapper.PathSectionMapper;
import subway.db.h2.repository.SubwaySectionJpaRepository;
import subway.domain.PathSearcher;
import subway.domain.PathSection;
import subway.domain.PathShortestDistanceSearcher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PersistenceAdapter
@RequiredArgsConstructor
public class PathSearcherLoadPersistenceAdapter implements PathSearcherLoadPort {

    private final SubwaySectionJpaRepository sectionRepository;
    private final PathSectionMapper pathSectionMapper;


    @Override
    public Optional<PathSearcher> findOne() {
        List<SubwaySectionJpa> subwaySectionJpas = sectionRepository.findAll();
        List<PathSection> sections = mapToPathSections(subwaySectionJpas);
        return Optional.of(PathShortestDistanceSearcher.from(sections));
    }

    private List<PathSection> mapToPathSections(List<SubwaySectionJpa> subwaySectionJpas) {
        return subwaySectionJpas.stream().map(pathSectionMapper::from).collect(Collectors.toList());
    }
}
