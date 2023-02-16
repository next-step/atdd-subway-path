package nextstep.step2.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("구간 관리 - 추가 도메인 테스트")
public class SectionTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("구간 제거: 삭제할 역이 노선에 존재하지 않는 경우")
    void deleteSection_fail_stationNotExist() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("구간 제거: 구간이 1개일 경우")
    void deleteSection_success_oneSectionExist() {
        // given

        // when

        // then
    }
}
