package subway.db.h2.adapter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import subway.application.query.out.PathSearcherLoadPort;
import subway.common.annotation.UnitTest;
import subway.db.h2.entity.SubwaySectionJpa;
import subway.db.h2.mapper.PathSectionMapper;
import subway.db.h2.repository.SubwaySectionJpaRepository;
import subway.domain.PathSearcher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@UnitTest
@DisplayName("경로 검색 도메인 모델 조회 기능 테스트")
class PathSearcherLoadPersistenceAdapterTest {

    private SubwaySectionJpaRepository repository = Mockito.mock(SubwaySectionJpaRepository.class);
    private PathSectionMapper mapper = new PathSectionMapper();
    private PathSearcherLoadPort port = new PathSearcherLoadPersistenceAdapter(repository, mapper);

    /**
     * @given 지하철 노선이 존재한다면
     * @when 경로 검색 도메인 모델을 조회할 때
     * @then 결과를 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선이 존재한다면 경로 검색 도메인 모델을 조회할 수 있다.")
    void findOne() {
        //given
        SubwaySectionJpa 첫번째구간 = SubwaySectionJpa.of(1L, 1L, "서울대입구역", 2L, "봉천역", BigDecimal.valueOf(10));
        SubwaySectionJpa 두번째구간 = SubwaySectionJpa.of(2L, 2L, "봉천역", 2L, "신림역", BigDecimal.valueOf(10));

        given(repository.findAll()).willReturn(List.of(첫번째구간, 두번째구간));

        //when
        Optional<PathSearcher> optionalSearcher = port.findOne();

        //then
        assertThat(optionalSearcher.isPresent()).isTrue();
    }

}