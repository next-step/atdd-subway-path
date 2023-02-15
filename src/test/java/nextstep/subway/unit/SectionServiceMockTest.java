package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Sections;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static nextstep.subway.fixture.LineFixture.이호선;
import static nextstep.subway.fixture.SectionFixture.강남_역삼_구간;
import static nextstep.subway.fixture.StationFixture.강남역;
import static nextstep.subway.fixture.StationFixture.선릉역;
import static nextstep.subway.fixture.StationFixture.역삼역;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("지하철 구간 서비스 단위 테스트 (Mock 객체)")
@ExtendWith(MockitoExtension.class)
class SectionServiceMockTest {

    @InjectMocks // 실제 객체를 생성 후 모의 의존성을 주입 (@Spy or @Mock으로 선언한 객체만 주입)
    private SectionService sectionService;
    @Mock
    private StationService stationService;
    @Mock
    private LineService lineService;

    @Mock // 간접 의존 객체 목킹이 안먹힘..
    private Sections sections;

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        Long 노선_id = 1L;
        Long 상행역_id = 1L;
        Long 하행역_id = 2L;

        // given - 내부 의존 객체 stub 설정을 통해 초기값 셋팅
        given(stationService.findById(상행역_id))
                .willReturn(강남역.엔티티_생성());
        given(stationService.findById(하행역_id))
                .willReturn(역삼역.엔티티_생성());
        given(lineService.findLineById(노선_id))
                .willReturn(이호선.엔티티_생성());

        // when - addSection 호출
        sectionService.addSection(노선_id, new SectionRequest(상행역_id, 하행역_id, 강남_역삼_구간.노선_간_거리()));

        // then - 의존성 객체에 대한 행위 검증
        verify(stationService).findById(상행역_id);
        verify(stationService).findById(하행역_id);
        verify(lineService).findLineById(노선_id);
    }

    @DisplayName("지하철 구간 삭제")
    @Test
    @Disabled("간접 의존 객체에 대한 stub 처리 방법 질문해야됨")
    void deleteSection() {
        Long 노선_id = 1L;
        Long 삭제할_역_id = 1L;

        // given - 내부 의존 객체 stub 설정을 통해 초기값 셋팅
        given(lineService.findLineById(노선_id))
                .willReturn(이호선.엔티티_생성());
        given(stationService.findById(삭제할_역_id))
                .willReturn(선릉역.엔티티_생성());
        given(sections.isEmpty())
                .willReturn(false);

        // when - deleteSection 호출
        // 의존 객체인 도메인 객체 Sections.removeLastSection() 에서 getLastSection 부분에 isEmpty()를 어떻게 모킹하지..?
        sectionService.deleteSection(노선_id, 삭제할_역_id);

        // then - 의존성 객체에 대한 행위 검증
        verify(lineService).findLineById(노선_id);
        verify(stationService).findById(삭제할_역_id);
    }
}
