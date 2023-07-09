package nextstep.subway.domain.line;

import org.springframework.data.jpa.repository.JpaRepository;

import nextstep.subway.domain.line.exception.NoSuchLineException;

public interface LineRepository extends JpaRepository<Line, Long> {

    default Line getById(final Long id) throws NoSuchLineException {
        return findById(id).orElseThrow(() -> NoSuchLineException.from(id));
    }
}
