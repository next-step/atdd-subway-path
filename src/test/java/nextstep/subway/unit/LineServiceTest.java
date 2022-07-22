package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private final int DEFAULT_DISTANCE = 10;

    @Test
    @DisplayName("구간을 생성한다.")
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 개봉역 = stationRepository.save(new Station("개봉역"));
        Station 구일역 = stationRepository.save(new Station("구일역"));

        Line 일호선 = lineRepository.save(new Line("일호선", "blue"));

        // when
        // lineService.addSection 호출
        lineService.addSection(일호선.getId(),
                new SectionRequest(개봉역.getId(), 구일역.getId(), DEFAULT_DISTANCE));

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(
                () -> assertThat(일호선.getSections()).hasSize(1),
                () -> assertThat(일호선.getSections().get(0).getUpStation().getName()).containsAnyOf("개봉역"),
                () -> assertThat(일호선.getSections().get(0).getDownStation().getName()).containsAnyOf("구일역")
        );

    }


    @Test
    @DisplayName("실패케이스1 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음.")
    void addSectionFail1() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 개봉역 = stationRepository.save(new Station("개봉역"));
        Station 구일역 = stationRepository.save(new Station("구일역"));
        Station 구로역 = stationRepository.save(new Station("구로역"));
        Line 일호선 = lineRepository.save(new Line("일호선", "blue"));

        // when
        // lineService.addSection 호출
        lineService.addSection(일호선.getId(),
                new SectionRequest(개봉역.getId(), 구일역.getId(), DEFAULT_DISTANCE));



        // then
        // line.getSections 메서드를 통해 검증
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> {
                    lineService.addSection(일호선.getId(),
                            new SectionRequest(개봉역.getId(), 구로역.getId(), DEFAULT_DISTANCE));
                });

    }

    @Test
    @DisplayName("실패케이스2 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    void addSectionFail2() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 개봉역 = stationRepository.save(new Station("개봉역"));
        Station 구일역 = stationRepository.save(new Station("구일역"));
        Line 일호선 = lineRepository.save(new Line("일호선", "blue"));

        // when
        // lineService.addSection 호출
        lineService.addSection(일호선.getId(),
                new SectionRequest(개봉역.getId(), 구일역.getId(), DEFAULT_DISTANCE));



        // then
        // line.getSections 메서드를 통해 검증
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> {
                    lineService.addSection(일호선.getId(),
                            new SectionRequest(개봉역.getId(), 구일역.getId(), DEFAULT_DISTANCE));
                });

    }

    @Test
    @DisplayName("실패케이스3 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    void addSectionFail3() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 개봉역 = stationRepository.save(new Station("개봉역"));
        Station 구일역 = stationRepository.save(new Station("구일역"));
        Station 합정역 = stationRepository.save(new Station("구로역"));
        Station 상수역 = stationRepository.save(new Station("구로역"));
        Line 일호선 = lineRepository.save(new Line("일호선", "blue"));

        // when
        // lineService.addSection 호출
        lineService.addSection(일호선.getId(),
                new SectionRequest(개봉역.getId(), 구일역.getId(), DEFAULT_DISTANCE));



        // then
        // line.getSections 메서드를 통해 검증
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> {
                    lineService.addSection(일호선.getId(),
                            new SectionRequest(합정역.getId(), 상수역.getId(), DEFAULT_DISTANCE));
                });

    }
}
