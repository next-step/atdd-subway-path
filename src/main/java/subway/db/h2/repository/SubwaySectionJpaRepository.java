package subway.db.h2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import subway.db.h2.entity.SubwaySectionJpa;

public interface SubwaySectionJpaRepository extends JpaRepository<SubwaySectionJpa, Long> {
}
