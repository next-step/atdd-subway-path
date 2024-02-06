package nextstep.subway.unit;

import nextstep.subway.dto.SectionRequest;
import nextstep.subway.entity.Line;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.service.LineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private static final Long 첫번째_역_id = 1L;
    private static final Long 두번째_역_id = 2L;
    private static final Long 세번째_역 = 3L;
    private Line 노선;

    @BeforeEach
    void setup() {
        // given
        노선 = lineRepository.save(new Line("노선", "빨강", 첫번째_역_id, 두번째_역_id, 1));
    }

    @Test
    @DisplayName("지하철의 노선의 구간을 등록한다.")
    void addSection() {
        // when
        lineService.addSection(노선.getId(), new SectionRequest(세번째_역, 두번째_역_id, 1));

        // then
        assertThat(노선.getDistance()).isEqualTo(2);
        assertTrue(노선.hasStation(세번째_역));
    }

    @Test
    @DisplayName("지하철 노선의 구간을 제거한다.")
    void deleteSection() {
        // given
        lineService.addSection(노선.getId(), new SectionRequest(세번째_역, 두번째_역_id, 1));

        // when
        lineService.deleteSection(노선.getId(), 세번째_역);

        // then
        assertFalse(노선.hasStation(세번째_역));
    }
}
