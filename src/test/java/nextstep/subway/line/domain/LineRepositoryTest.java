package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("지하철 노선이 존재하지 않을 경우")
    void nonExistLineName() {
        // given
        String lineName = "신분당선";

        // when
        boolean result = lineRepository.existsByName(lineName);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("지하철 노선이 존재할 경우")
    void existLineName() {
        // given
        Line savedLine = lineRepository.save(new Line("신분당선", "bg-red-600"));

        // when
        boolean result = lineRepository.existsByName(savedLine.getName());

        // then
        assertThat(result).isTrue();
    }
}
