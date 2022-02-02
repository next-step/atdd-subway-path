package nextstep.subway.applicaion;

import nextstep.fake.LineFakeRepository;
import nextstep.fake.StationFakeRepository;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static nextstep.subway.utils.LineStepUtil.기존노선;
import static nextstep.subway.utils.LineStepUtil.기존색상;
import static nextstep.subway.utils.StationStepUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class LineServiceTest {


    LineFakeRepository lineFakeRepository = new LineFakeRepository();

    StationFakeRepository stationFakeRepository = new StationFakeRepository();

    LineService lineService;

    Station 기존_지하철;
    Station 새로운_지하철;

    @BeforeEach
    void setUp() {
        기존_지하철 = stationFakeRepository.save(new Station(기존지하철));
        새로운_지하철 = stationFakeRepository.save(new Station(새로운지하철));
        lineService = new LineService(lineFakeRepository, stationFakeRepository);
    }

    /**
     * Given 구간을 생성한다.
     * When  구간을 저장한다.
     * Then  구간이 저장된다.
     */
    @Test
    void 노선_등록() {
        //given
        LineRequest 라인_등록_파라미터 = LineRequest.of(기존노선, 기존색상,기존_지하철,새로운_지하철, 역간_거리);

        //when
        LineResponse 라인_등록_응답 = lineService.saveLine(라인_등록_파라미터);

        //then
        assertThat(라인_등록_응답.getColor()).isEqualTo(기존색상);
    }


    @Test
    void update() {
    }

}
