package nextstep.subway.unit;


import nextstep.subway.application.PathService;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PathServiceTest {
    @Autowired
    private PathService pathService;

    @DisplayName("최단거리 경로를 조회시, 지하철역이 존재 하지 않으면 예외가 발생한다.")
    @Test
    void findPath_invalid_not_found_station() {
        // Given
        Long invalidSource = 99L;
        Long invalidTarget = 99L;

        // When
        assertThatThrownBy(() -> { pathService.findPath(invalidSource, invalidTarget); })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재 하지 않는 지하철역 입니다.");
    }

}
