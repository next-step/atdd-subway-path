package nextstep.subway.path.application;

import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.dto.PathResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@DisplayName("경로 탐색 서비스 유닛 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private LineRepository lineRepository;

    private PathService pathService;

    @BeforeEach
    void setUp() {
        pathService = new PathService(lineRepository);
    }

    @DisplayName("경로를 잘 찾는지 검사한다.")
    @Test
    void find_path() {

        // when
        final PathResponse pathResponse = pathService.findShortestPath(3, 5);

        // then
        assertThat(pathResponse).isNotNull();
        verify(lineRepository.findAll());
    }
}