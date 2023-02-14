package nextstep.subway.domain;

import nextstep.subway.fixture.LineFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static nextstep.subway.fixture.LineFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class SectionRepositoryTest {

    @Autowired
    private LineRepository repository;

    @Test
    void findByIdStationIds() {

        Line line1 = repository.save(createLineWithSection(null, null, null));
        Line line2 = repository.save(createLineWithSection(null, null, null));

        Set<Line> lines = repository.findByStationIds(List.of(
                line1.getStations().get(0).getId(),
                line2.getStations().get(0).getId()));

        assertThat(lines).hasSize(2);
    }
}