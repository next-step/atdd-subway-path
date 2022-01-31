package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Override
    List<Section> findAll();
}
