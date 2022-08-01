package nextstep.subway.unit;

import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.dto.SectionDto;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SectionServiceMockTest {

    @Mock
    SectionRepository sectionRepository;

    @InjectMocks
    SectionService sectionService;

    Section 강남_선릉_10;
    Section 선릉_왕십리_10;
    Section 선릉_왕십리_5;

    @BeforeEach
    void setUp() {
        강남_선릉_10 = new Section(FakeLineFactory.분당선(), FakeStationFactory.강남역(), FakeStationFactory.선릉역(), 10);
        선릉_왕십리_10 = new Section(FakeLineFactory.분당선(), FakeStationFactory.선릉역(),  FakeStationFactory.왕십리역(), 10);
        선릉_왕십리_5 = new Section(FakeLineFactory.신분당선(), FakeStationFactory.선릉역(), FakeStationFactory.왕십리역(), 5);
    }

    @Test
    void 등록된_모든_구간을_조회한다() {
        //given
        when(sectionRepository.findAll()).thenReturn(List.of(강남_선릉_10, 선릉_왕십리_10, 선릉_왕십리_5));

        //when
        List<SectionDto> sections = sectionService.findAll();

        //then
        assertThat(sections).hasSize(3);
    }
}
