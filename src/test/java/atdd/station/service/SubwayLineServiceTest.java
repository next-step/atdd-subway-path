package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
import atdd.station.dto.subwayLine.SubwayLineCreateRequestDto;
import atdd.station.dto.subwayLine.SubwayLineCreateResponseDto;
import atdd.station.dto.subwayLine.SubwayLineDetailResponseDto;
import atdd.station.dto.subwayLine.SubwayLineListResponseDto;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static atdd.station.fixture.StationFixture.KANGNAM_AND_YUCKSAM_STATIONS;
import static atdd.station.fixture.SubwayLineFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
public class SubwayLineServiceTest {

    @Mock
    SubwayLineRepository subwayLineRepository;

    @InjectMocks
    SubwayLineService subwayLineService;

    private static final long DEFAULT_ID = 0L;

    @Test
    public void 지하철노선_생성시_성공하는지(SoftAssertions softly) {
        //given
        SubwayLine subwayLine = getSecondSubwayLineName();

        //when
        when(subwayLineRepository.save(any())).thenReturn(subwayLine);

        SubwayLineCreateResponseDto createSubwayLine
                = subwayLineService.create(SubwayLineCreateRequestDto.toDtoEntity(subwayLine, subwayLine.getSubways()));

        //then
        softly.assertThat(createSubwayLine.getId()).isEqualTo(DEFAULT_ID);
        softly.assertThat(createSubwayLine.getName()).isEqualTo(SECOND_SUBWAY_LINE_NAME);
    }

    @Test
    public void 지하철노선_list_조회가_성공하는지(SoftAssertions softly) {
        //given
        List<SubwayLine> subwayLines = getSubwayLines();

        //when
        when(subwayLineRepository.findAll()).thenReturn(subwayLines);

        SubwayLineListResponseDto listedSubwayLines = subwayLineService.list();

        //then
        softly.assertThat(listedSubwayLines).isNotNull();
        softly.assertThat(listedSubwayLines.toString()).contains(FIRST_SUBWAY_LINE);
    }

    @Test
    public void 지하철노선_상세_조회가_성공하는지(SoftAssertions softly) {
        //given
        SubwayLine subwayLine = getSecondSubwayLineName();

        //when
        when(subwayLineRepository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.ofNullable(subwayLine));

        SubwayLineDetailResponseDto detailSubwayLine = subwayLineService.detail(DEFAULT_ID);

        //then
        softly.assertThat(detailSubwayLine).isNotNull();
        softly.assertThat(detailSubwayLine.getName()).contains(SECOND_SUBWAY_LINE_NAME);
    }

    @Test
    public void 지하철노선_삭제가_성공하는지(SoftAssertions softly) {
        //given
        SubwayLine subwayLine = getSecondSubwayLineName();

        when(subwayLineRepository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(subwayLine));
        subwayLineService.delete(DEFAULT_ID);

        //then
        softly.assertThat(subwayLine.isDeleted()).isTrue();
    }


    @Test
    public void 지하철_2호선에_강남역_추가가_성공하는지(SoftAssertions softly) {
        //given
        SubwayLine subwayLine = getSubwayLine(SECOND_SUBWAY_LINE_NAME);
        List<Station> stations = KANGNAM_AND_YUCKSAM_STATIONS;

        when(subwayLineRepository.findById(DEFAULT_ID)).thenReturn(java.util.Optional.of(subwayLine));
        SubwayLine updatedSubwayLine = subwayLineService.update(stations);

        //then
        softly.assertThat(subwayLine.isDeleted()).isTrue();
    }

}
