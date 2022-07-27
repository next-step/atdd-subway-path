package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.unit.LineTestUtil.DEFAULT_DISTANCE;
import static nextstep.subway.unit.LineTestUtil.개봉역;
import static nextstep.subway.unit.LineTestUtil.구로역;
import static nextstep.subway.unit.LineTestUtil.구일역;
import static nextstep.subway.unit.LineTestUtil.라인색;
import static nextstep.subway.unit.LineTestUtil.일호선ID;
import static nextstep.subway.unit.LineTestUtil.일호선이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class LineSectionFailTest {

    Line 일호선 = new Line(일호선ID, 일호선이름, 라인색  );

    @BeforeEach
    void setUp(){
        일호선.addSection(1L, 개봉역, 구로역, DEFAULT_DISTANCE);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void sectionAddFail() {
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> {
                    일호선.addSection(2L, 개봉역, 구일역, 15);
                });
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void sectionAddFail2() {
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> {
                    일호선.addSection(2L, 개봉역, 구로역, 15);
                });
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void sectionAddFail3() {
        Station 신도림역 = new Station(3L, "신도림역");
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> {
                    일호선.addSection(2L, 구일역, 신도림역, 15);
                });
    }

}
