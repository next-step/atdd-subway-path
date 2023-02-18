package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import nextstep.subway.exception.InvalidSectionDistanceException;
import nextstep.subway.exception.SectionContainsAllStationException;
import nextstep.subway.exception.SectionContainsAnyStationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class LineServiceTest {

    private Station 강남역;
    private Station 분당역;

    private Line 신분당선;

    private SectionRequest 구간_요청;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void setUp() {
        //Given
        강남역 = stationRepository.save(new Station("강남역"));
        분당역 = stationRepository.save(new Station("분당역"));
        신분당선 = lineRepository.save(new Line("신분당선", "green"));
        구간_요청 = new SectionRequest(강남역.getId(), 분당역.getId(), 10);
        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), 구간_요청);
    }

    @DisplayName("노선 생성 후 첫 구간 추가")
    @Test
    void addSection() {
        // then
        // line.getSections 메서드를 통해 검증
        Section 새로운_구간 = 신분당선.getFirstSection();
        assertThat(새로운_구간.getLine()).isEqualTo(신분당선);
        assertThat(새로운_구간.getUpStation()).isEqualTo(강남역);
        assertThat(새로운_구간.getDownStation()).isEqualTo(분당역);
        assertThat(새로운_구간.getDistance()).isEqualTo(10);
    }

    @DisplayName("기존 역 사이에 새로운 역 추가")
    @Test
    void addSection2() {
        //when
        //강남역과 분당역 사이에 정자역 추가
        Station 정자역 = stationRepository.save(new Station("정자역"));
        lineService.addSection(신분당선.getId(),
                new SectionRequest(강남역.getId(), 정자역.getId(), 4));

        //then
        assertThat(신분당선.getFirstSection().getDistance()).isEqualTo(4);
        assertThat(신분당선.getLastSection().getDistance()).isEqualTo(6);
        assertThat(신분당선.getDownStation()).isEqualTo(분당역);
        assertThat(신분당선.getSections()).hasSize(2);
    }

    @DisplayName("새로운 역을 상행 종점역에 등록")
    @Test
    void addSection3() {
        //when
        //강남역과 분당역 사이에 정자역 추가
        Station 정자역 = stationRepository.save(new Station("정자역"));
        lineService.addSection(신분당선.getId(),
                new SectionRequest(분당역.getId(), 정자역.getId(), 4));

        //then
        assertThat(신분당선.getDownStation()).isEqualTo(정자역);
    }

    @DisplayName("새로운 역을 하행 종점역에 등록")
    @Test
    void addSection4() {
        //when
        //강남역과 분당역 사이에 정자역 추가
        Station 정자역 = stationRepository.save(new Station("정자역"));
        lineService.addSection(신분당선.getId(),
                new SectionRequest(정자역.getId(), 강남역.getId(), 4));

        //then
        assertThat(신분당선.getUpStation()).isEqualTo(정자역);
    }


    @DisplayName("역 사이에 새로운 역을 등록할 때, 기존 역 사이 길이보다 크거나 같다.")
    @Test
    void addLongerSection() {
        // when
        // 첫 구간 추가
        lineService.addSection(신분당선.getId(), 구간_요청);

        //when
        //강남역과 분당역 사이에 정자역 추가 요청
        Station 정자역 = stationRepository.save(new Station("정자역"));
        SectionRequest 새로운_요청 = new SectionRequest(강남역.getId(), 정자역.getId(), 10);

        //then
        assertThatThrownBy(()->
                lineService.addSection(신분당선.getId(), 새로운_요청)
        ).isInstanceOf(InvalidSectionDistanceException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있을 때 예외 발생.")
    @Test
    void addSectionContainsAllStation() {
        // when
        // 첫 구간 추가
        lineService.addSection(신분당선.getId(), 구간_요청);

        //when
        //새로운 노선이 기존의 역을 모두 포함
        SectionRequest 새로운_요청 = new SectionRequest(강남역.getId(), 분당역.getId(), 10);

        //then
        assertThatThrownBy(()->
                lineService.addSection(신분당선.getId(), 새로운_요청)
        ).isInstanceOf(SectionContainsAllStationException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 예외 발생.")
    @Test
    void addSectionContainsNoStation() {
        // when
        // 첫 구간 추가
        lineService.addSection(신분당선.getId(), 구간_요청);

        //when
        //새로운 노선이 기존의 역을 모두 포함
        Station 정자역 = stationRepository.save(new Station("정자역"));
        Station 교대역 = stationRepository.save(new Station("교대역"));
        SectionRequest 새로운_요청 = new SectionRequest(정자역.getId(), 교대역.getId(), 10);

        //then
        assertThatThrownBy(()->
                lineService.addSection(신분당선.getId(), 새로운_요청)
        ).isInstanceOf(SectionContainsAnyStationException.class);
    }
}
