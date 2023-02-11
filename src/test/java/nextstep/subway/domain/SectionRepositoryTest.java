package nextstep.subway.domain;

import nextstep.subway.fixture.SectionFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static nextstep.subway.fixture.LineFixture.createLine;
import static nextstep.subway.fixture.SectionFixture.createSection;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SectionRepositoryTest {

    @Autowired
    private SectionRepository repository;

    @Test
    void findByIdStationIds() {
        Section save1 = repository.save(createLine(null, null).getSections().getLast());
        Section save2 = repository.save(createLine(null, null).getSections().getLast());

        List<Section> sections = repository.findByIdStationIds(List.of(save1.getUpStationId(), save2.getUpStationId()));
        assertThat(sections).hasSize(2);
    }
}