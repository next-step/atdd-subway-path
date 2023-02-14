package nextstep.subway.domain;

import nextstep.subway.fixture.LineFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SectionRepositoryTest {

    @Autowired
    private SectionRepository repository;

    @Test
    void findByIdStationIds() {
        Section save1 = repository.save(LineFixture.createLineWithSection(null,null, null).getSections().getLast());
        Section save2 = repository.save(LineFixture.createLineWithSection(null,null, null).getSections().getLast());

        List<Section> sections = repository.findByIdStationIds(List.of(save1.getUpStationId(), save2.getUpStationId()));
        assertThat(sections).hasSize(2);
    }
}