package nextstep.subway.unit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.common.DomainException;
import nextstep.subway.common.DomainExceptionType;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("노선 서비스 테스트 (Classist)")
public class LineServiceTest {
    @Autowired private StationRepository stationRepository;
    @Autowired private LineRepository lineRepository;
    @Autowired private LineService lineService;

    private Line 이호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 삼성역;
    private Section 강남_역삼_구간;
    private Section 역삼_선릉_구간;

    @BeforeEach
    void setUp() {
        이호선 = new Line("이호선", "#29832");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        삼성역 = new Station("삼성역");
        강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 10);
        역삼_선릉_구간 = new Section(이호선, 역삼역, 선릉역, 5);
    }

    @Test
    @DisplayName("구간 추가 테스트")
    void addSection() {
        // given
        강남역 = stationRepository.save(강남역);
        역삼역 = stationRepository.save(역삼역);

        이호선 = lineRepository.save(이호선);

        // when
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 5));

        // then
        Line resultLine =
                lineRepository
                        .findById(이호선.getId())
                        .orElseThrow(() -> new DomainException(DomainExceptionType.NO_LINE));
        assertThat(resultLine.getSections()).extracting(Section::getDownStation).contains(역삼역);
    }
}
