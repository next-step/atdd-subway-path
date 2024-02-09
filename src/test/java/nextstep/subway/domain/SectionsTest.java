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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SectionsTest {

    private Sections 구간;
    private Section 강남역_선릉역_구간;
    private Section 선릉역_양재역_구간;
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
        강남역_선릉역_구간 = new Section(
                신분당선,
                강남역,
                선릉역,
                10L
        );
        선릉역_양재역_구간 = new Section(
                신분당선,
                선릉역,
                양재역,
                10L
        );
        구간 = new Sections(List.of(강남역_선릉역_구간, 선릉역_양재역_구간));
    }

    @Test
    void 실패_구간_정보가_없을경우_구간을_등록할_수_없다() {
        assertThatThrownBy(() -> new Sections(new ArrayList<>()))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("구간이 존재하지 않습니다.");
    }

    @Test
    void 실패_구간_등록시_신규_구간과_기존_구간의_상행역과_하행역이_일치할_경우_구간을_등록할_수_없다() {
        assertThatThrownBy(() -> 구간.validateRegisterStationBy(강남역, 선릉역))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("신규 구간이 기존 구간과 일치하여 구간을 생성할 수 없습니다.");
    }

    @Test
    void 실패_구간_등록시_신규_구간과_기존_구간의_상행역과_하행역이_포함될_경우_구간을_등록할_수_없다() {
        assertThatThrownBy(() -> 구간.validateRegisterStationBy(선릉역, 강남역))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("신규 구간이 기존 구간과 일치하여 구간을 생성할 수 없습니다.");
    }

    @Test
    void 성공_구간_등록시_상행역을_기준으로_노선의_가운데에_구간을_등록할_수_있다() {
        구간.addSectionInMiddle(강남역, 역삼역, 3L);
        assertThat(강남역_선릉역_구간.upStation()).isEqualTo(역삼역);
        assertThat(강남역_선릉역_구간.downStation()).isEqualTo(선릉역);
        assertThat(강남역_선릉역_구간.distance()).isEqualTo(7L);
    }

    @Test
    void 성공_구간_등록시_하행역을_기준으로_노선의_가운데에_구간을_등록할_수_있다() {
        구간.addSectionInMiddle(역삼역, 양재역, 3L);
        assertThat(선릉역_양재역_구간.upStation()).isEqualTo(선릉역);
        assertThat(선릉역_양재역_구간.downStation()).isEqualTo(역삼역);
        assertThat(선릉역_양재역_구간.distance()).isEqualTo(7L);
    }

    @Test
    void 성공_노선의_구간에서_신규_구간의_상행역이_기존_구간의_상행역과_일치하는_구간을_찾는다() {
        Section matchingSection = 구간.findMatchingSection(강남역);
        assertThat(matchingSection).isEqualTo(강남역_선릉역_구간);
    }

    @Test
    void 실패_구간_제거시_구간이_한개만_있을경우_구간을_제거할_수_없다() {
        assertThatThrownBy(() -> new Sections(List.of(강남역_선릉역_구간)).validateDeleteSection(3L))
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
