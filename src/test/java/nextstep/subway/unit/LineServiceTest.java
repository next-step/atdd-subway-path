package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static nextstep.subway.fixture.LineFixture.*;
import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LineServiceTest {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;


    @BeforeEach
    void setUp() {
        // 데이터 베이스를 삭제한다.
        databaseCleanup.execute();

        // given 지하철역이 이미 생성되어 있다.
        stationRepository.save(강남역);
        stationRepository.save(역삼역);
        stationRepository.save(선릉역);
    }

    @Test
    @DisplayName("새로운 노선을 저장")
    void saveLine() {
        // given
        LineRequest request = LineRequest.builder()
                .name(이호선_이름).color(이호선_색).upStationId(강남역.getId()).downStationId(역삼역.getId()).distance(거리_10).build();

        // when
        LineResponse response = lineService.saveLine(request);

        // then
        assertThat(response.getName()).isNotNull();
        assertThat(response.getName()).isEqualTo(이호선_이름);
    }

    @Test
    @DisplayName("기존 노선을 수정")
    void updateLine() {
        // given
        lineRepository.save(이호선);
        LineRequest request = LineRequest.builder()
                .name(신분당선_이름).color(신분당선_색).build();

        // when
        lineService.updateLine(이호선.getId(), request);

        // then
        Line line = lineRepository.findById(이호선.getId()).orElseThrow();

        assertThat(line.getName()).isEqualTo(신분당선_이름);
        assertThat(line.getColor()).isEqualTo(신분당선_색);
    }

    @Test
    @DisplayName("기존 노선을 삭제")
    void deleteLine() {
        // given
        lineRepository.save(이호선);
        // when
        lineService.deleteLine(이호선.getId());

        // then
        assertThatThrownBy(() -> lineRepository.findById(이호선.getId()).orElseThrow()).isInstanceOf(NoSuchElementException.class);
    }


    @Test
    @DisplayName("기존 노선에 구간을 추가")
    void addSection() {
        // given
        lineRepository.save(이호선);
        이호선.addSections(new Section(이호선, 강남역, 역삼역, 거리_10));
        SectionRequest request = new SectionRequest(역삼역.getId(), 선릉역.getId(), 거리_10);

        // when
        lineService.addSection(이호선.getId(), request);


        // then
        assertThat(이호선).isNotNull();
        assertThat(이호선.getStartStation()).isEqualTo(강남역);
        assertThat(이호선.getEndStation()).isEqualTo(선릉역);
    }
}
