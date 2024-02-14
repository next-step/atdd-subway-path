package nextstep.subway.unit.domain;

import nextstep.subway.common.Constant;
import nextstep.subway.exception.AlreadyExistDownStationException;
import nextstep.subway.exception.AlreadyExistSectionException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class SectionsTest {

    private Line 신분당선;
    private Station 논현역;
    private Station 신논현역;
    private Station 강남역;
    private Station 양재역;
    private Station 신사역;
    private Section 논현_신논현_구간;
    private Section 신논현_강남_구간;
    private Section 논현_강남_구간;
    private Section 강남_신논현_구간;
    private Section 신사_논현_구간;

    @BeforeEach
    protected void setUp() {
        신분당선 = Line.of(Constant.신분당선, Constant.빨간색);
        논현역 = Station.from(Constant.논현역);
        신논현역 = Station.from(Constant.신논현역);
        강남역 = Station.from(Constant.강남역);
        양재역 = Station.from(Constant.양재역);
        신사역 = Station.from(Constant.신사역);
        논현_신논현_구간 = Section.of(논현역, 신논현역, Constant.기본_역_간격);
        신논현_강남_구간 = Section.of(신논현역, 강남역, Constant.기본_역_간격);
        논현_강남_구간 = Section.of(논현역, 강남역, Constant.기본_역_간격);
        강남_신논현_구간 = Section.of(강남역, 신논현역, Constant.기본_역_간격);
        신사_논현_구간 = Section.of(신사역, 논현역, Constant.기본_역_간격);
    }

    @DisplayName("노선 마지막에 구간 등록")
    @Test
    void 노선_마지막에_구간_등록() {
        // given
        Sections 신분당선_구간들 = Sections.from(new LinkedList(Arrays.asList(논현_신논현_구간)));

        // when
        신분당선_구간들.addSection(신논현_강남_구간);

        // then
        List<Section> 구간들 = 신분당선_구간들.getSections();
        Assertions.assertThat(구간들).containsOnlyOnce(논현_신논현_구간, 신논현_강남_구간);
    }

    @DisplayName("노선 중간에 구간 등록")
    @Test
    void 노선_중간에_구간_등록() {
        // given
        Sections 신분당선_구간들 = Sections.from(new LinkedList(Arrays.asList(논현_신논현_구간))); // 논현 - 신논현

        // when
        신분당선_구간들.addSection(논현_강남_구간); // 논현 - 강남 - 신논현

        // then
        List<Section> 구간들 = 신분당선_구간들.getSections();
        Assertions.assertThat(구간들).containsOnlyOnce(논현_강남_구간, 강남_신논현_구간);
    }

    @DisplayName("노선 처음에 구간 등록")
    @Test
    void 노선_처음에_구간_등록() {
        // given
        Sections 신분당선_구간들 = Sections.from(new LinkedList(Arrays.asList(논현_신논현_구간))); // 논현 - 신논현

        // when
        신분당선_구간들.addSection(신사_논현_구간); // 신사 - 논현 - 신논현

        // then
        List<Section> 구간들 = 신분당선_구간들.getSections();
        Assertions.assertThat(구간들).containsOnlyOnce(신사_논현_구간, 논현_신논현_구간);
    }

    @DisplayName("이미 등록된 구간을 등록하면 예외발생")
    @Test
    void 이미_등록된_구간을_등록하면_예외발생() {
        // given
        Sections 신분당선_구간들 = Sections.from(new LinkedList(Arrays.asList(논현_신논현_구간))); // 논현 - 신논현

        // when & then
        assertThatThrownBy(() -> 신분당선_구간들.addSection(논현_신논현_구간))
                .isInstanceOf(AlreadyExistSectionException.class);
    }

    @DisplayName("상행역과 하행역이 모두 노선에 없는 구간을 등록하면 예외발생")
    @Test
    void 상행역과_하행역이_모두_노선에_없는_구간을_등록하면_예외발생() {
    }



}
