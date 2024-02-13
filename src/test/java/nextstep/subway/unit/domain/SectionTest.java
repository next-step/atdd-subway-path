package nextstep.subway.unit.domain;

import nextstep.subway.common.Constant;
import nextstep.subway.exception.NotMatchUpStationAndDownStationException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class SectionTest {

    Station 논현역;
    Station 신논현역;
    Station 강남역;
    Station 양재역;

    @BeforeEach
    protected void setUp() {
        논현역 = Station.from(Constant.논현역);
        신논현역 = Station.from(Constant.신논현역);
        강남역 = Station.from(Constant.강남역);
        양재역 = Station.from(Constant.양재역);
    }

    @DisplayName("구간의 하행역 수정")
    @Test
    void 구간의_하행역_수정() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.기본_역_간격);

        // when
        신논현역_강남역_구간.updateDownStation(양재역);

        // then
        assertThat(신논현역_강남역_구간.isDownStation(양재역)).isTrue();
    }

    @DisplayName("구간의 상행역 수정")
    @Test
    void 구간의_상행역_수정() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.기본_역_간격);

        // when
        신논현역_강남역_구간.updateUpStation(논현역);

        // then
        assertThat(신논현역_강남역_구간.isUpStation(논현역)).isTrue();
    }

    @DisplayName("구간의 상행역과 수정할 하행역이 같으면 예외발생")
    @Test
    void 구간의_상행역과_수정할_하행역이_같으면_예외발생() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.기본_역_간격);

        // when & then
        assertThatThrownBy(() -> 신논현역_강남역_구간.updateDownStation(신논현역))
                .isInstanceOf(NotMatchUpStationAndDownStationException.class);
    }

    @DisplayName("구간의 하행역과 수정할 상행역이 같으면 예외발생")
    @Test
    void 구간의_하행역과_수정할_상행역이_같으면_예외발생() {
        // given
        Section 신논현역_강남역_구간 = Section.of(신논현역, 강남역, Constant.기본_역_간격);

        // when & then
        assertThatThrownBy(() -> 신논현역_강남역_구간.updateUpStation(강남역))
                .isInstanceOf(NotMatchUpStationAndDownStationException.class);
    }

}
