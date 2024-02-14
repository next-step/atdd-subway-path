package nextstep.subway.unit;

import nextstep.config.annotations.ApplicationTest;
import nextstep.config.fixtures.SectionFixture;
import nextstep.config.fixtures.StationFixture;
import nextstep.subway.application.LineService;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.repository.LineRepository;
import nextstep.subway.entity.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ApplicationTest
public class LineServiceTest {

    Long 이호선_아이디;
    Long 존재하지_않는_삼호선_아이디;

    Long 선릉역_번호;
    Long 삼성역_번호;
    Long 신천역_번호;
    Long 존재하지_않는_강남역_번호;

    Station 선릉역;
    Station 삼성역;
    Station 신천역;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void 사전_설정_역과_노선_추가() {
        선릉역 = StationFixture.선릉;
        삼성역 = StationFixture.삼성;
        신천역 = StationFixture.신천;

        선릉역_번호 = stationRepository.save(this.선릉역).getId();
        삼성역_번호 = stationRepository.save(this.삼성역).getId();
        신천역_번호 = stationRepository.save(this.신천역).getId();
        존재하지_않는_강남역_번호 = 4L;

        이호선_아이디 = lineRepository.save(new Line("이호선", "그린")).getId();
        존재하지_않는_삼호선_아이디= 2L;
    }

    @Nested
    class 구간_추가 {

        @Nested
        class 성공 {

            /**
             * Given 지하철 노선이 생성되고
             * When  지하철 구간을 추가하면
             * Then  지하철 노선에 구간이 추가된다.
             */
            @Test
            void 지하철_구간_추가() {
                // given
                SectionRequest 선릉_삼성_구간_요청 = SectionRequest.mergeForCreateLine(
                        이호선_아이디,
                        SectionFixture.지하철_구간(선릉역_번호, 삼성역_번호, 10));

                lineService.addSection(선릉_삼성_구간_요청);

                // then
                assertThat(lineService.findLineById(이호선_아이디).getStations())
                        .containsAnyOf(선릉역, 삼성역);
            }
        }

        @Nested
        class 실패 {
            
            /**
              * Given 지하철 노선이 생성되고
              * When  지하철 구간을 추가할 때
              * When    지하철 노선이 없는 경우
              * Then  지하철 노선에 구간을 추가할 수 없다.
              */
            @Test
            void 존재하지_않는_노선에_구간을_추가하는_경우() {
                // given
                SectionRequest 선릉_삼성_구간_요청 = SectionRequest.mergeForCreateLine(
                        존재하지_않는_삼호선_아이디,
                        SectionFixture.지하철_구간(선릉역_번호, 삼성역_번호, 10));

                // then
                assertThatExceptionOfType(EntityNotFoundException.class)
                        .isThrownBy(() -> {
                            lineService.addSection(선릉_삼성_구간_요청);
                        })
                        .withMessageMatching("노선 번호에 해당하는 노선이 없습니다.");
            }

            /**
             * Given 지하철 노선이 생성되고
             * When  지하철 구간을 추가할 때
             * When    추가할 구간의 상행역이 존재하지 않는 경우
             * Then  지하철 노선에 구간을 추가할 수 없다.
             */
            @Test
            void 추가할_구간의_상행역이_존재하지_않는_경우() {
                // given
                SectionRequest 강남_삼성_구간_요청 = SectionRequest.mergeForCreateLine(
                        이호선_아이디,
                        SectionFixture.지하철_구간(존재하지_않는_강남역_번호, 삼성역_번호, 10));

                // then
                assertThatExceptionOfType(EntityNotFoundException.class)
                        .isThrownBy(() -> {
                            lineService.addSection(강남_삼성_구간_요청);
                        })
                        .withMessageMatching("역 번호에 해당하는 역이 없습니다.");
            }

            /**
             * Given 지하철 노선이 생성되고
             * When  지하철 구간을 추가할 때
             * When    추가할 구간의 하행역이 존재하지 않을 경우
             * Then  지하철 노선에 구간을 추가할 수 없다.
             */
            @Test
            void 추가할_구간의_하행역이_존재하지_않는_경우() {
                // given
                SectionRequest 선릉_강남_구간_요청 = SectionRequest.mergeForCreateLine(
                        이호선_아이디,
                        SectionFixture.지하철_구간(선릉역_번호, 존재하지_않는_강남역_번호, 10));

                // then
                assertThatExceptionOfType(EntityNotFoundException.class)
                        .isThrownBy(() -> {
                            lineService.addSection(선릉_강남_구간_요청);
                        })
                        .withMessageMatching("역 번호에 해당하는 역이 없습니다.");
            }
        }
    }

    /**
     * Given 지하철 노선이 생성되고 구간을 추가한다
     * When  지하철 구간을 삭제하면
     * Then  지하철 노선에 구간이 삭제된다
     */
    @Test
    void 지하철_구간_삭제() {
        // given
        SectionRequest 선릉_삼성_구간_요청 = SectionRequest.mergeForCreateLine(
                이호선_아이디,
                SectionFixture.지하철_구간(선릉역_번호, 삼성역_번호, 10));
        SectionRequest 삼성_신천_구간_요청 = SectionRequest.mergeForCreateLine(
                이호선_아이디,
                SectionFixture.지하철_구간(삼성역_번호, 신천역_번호, 10));

        lineService.addSection(선릉_삼성_구간_요청);
        lineService.addSection(삼성_신천_구간_요청);

        // when
        lineService.deleteSection(이호선_아이디, 신천역_번호);

        // then
        assertThat(lineService.findLineById(이호선_아이디).getStations())
                .containsAnyOf(선릉역, 삼성역);
    }
}
