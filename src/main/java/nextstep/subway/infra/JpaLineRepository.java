package nextstep.subway.infra;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaLineRepository extends JpaRepository<Line, Long>, LineRepository {
    @Override
    <S extends Line> S save(S entity);

    @Override
    List<Line> findAll();

    @Override
    Optional<Line> findById(Long aLong);

    @Override
    void deleteById(Long aLong);
}
