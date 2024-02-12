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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
public class LineServiceTest {

    Long 이호선_아이디;
    Long 선릉역_번호;
    Long 삼성역_번호;
    Long 신천역_번호;

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

        이호선_아이디 = lineRepository.save(new Line("이호선", "그린", 10)).getId();
    }

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
