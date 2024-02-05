package nextstep.subway.domain;

import nextstep.subway.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.fixture.LineFixture.SHINBUNDANG_LINE;
import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Sections 구간;
    private Station 강남역;
    private Station 선릉역;
    private Station 양재역;
    private Station 역삼역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = GANGNAM_STATION.toStation(1L);
        선릉역 = SEOLLEUNG_STATION.toStation(2L);
        양재역 = YANGJAE_STATION.toStation(3L);
        역삼역 = YEOKSAM_STATION.toStation(4L);
        신분당선 = SHINBUNDANG_LINE.toLine(1L);
        구간 = new Sections(List.of(new Section(
                신분당선,
                강남역,
                선릉역,
                10L
        )));
    }

    @Test
    void 실패_구간_정보가_없을경우_구간을_등록할_수_없다() {
        assertThatThrownBy(() -> new Sections(new ArrayList<>()))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("구간이 존재하지 않습니다.");
    }

    @Test
    void 실패_구간_등록시_새로운_구간의_상행역이_노선의_하행_종점역이_아닐경우_구간을_등록할_수_없다() {
        assertThatThrownBy(() -> 구간.validateRegisterStationBy(강남역, 양재역))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("새로운 구간의 상행역은 노선의 하행 종점역에만 생성할 수 있습니다.");
    }

    @Test
    void 실패_구간_등록시_새로운_구간의_하행역이_노선에_존재하는_역에_생성할_경우_구간을_등록할_수_없다() {
        assertThatThrownBy(() -> 구간.validateRegisterStationBy(양재역, 선릉역))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("새로운 구간의 하행역은 노선에 존재하는 역에 생성할 수 없습니다.");
    }

    @Test
    void 실패_구간_제거시_구간이_한개만_있을경우_구간을_제거할_수_없다() {
        assertThatThrownBy(() -> 구간.validateDeleteSection(3L))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("구간이 한개만 있을 경우 구간을 제거할 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void 실패_구간_제거시_마지막_구간이_아닐경우_구간을_제거할_수_없다(long sectionId) {
        Sections 구간 = 구간_3개_등록();

        assertThatThrownBy(() -> 구간.validateDeleteSection(sectionId))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("마지막 구간이 아닐 경우 구간을 제거할 수 없습니다.");
    }

    private Sections 구간_3개_등록() {
        return new Sections(List.of(
                new Section(
                        신분당선,
                        강남역,
                        선릉역,
                        10L
                ),
                new Section(
                        신분당선,
                        선릉역,
                        양재역,
                        10L
                ),
                new Section(
                        신분당선,
                        양재역,
                        역삼역,
                        10L
                )
        )
        );
    }
}
