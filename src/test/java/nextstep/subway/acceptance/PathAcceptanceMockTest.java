package nextstep.subway.acceptance;

import nextstep.subway.applicaion.PathService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static nextstep.subway.acceptance.PathSteps.경로_조회;

@ExtendWith(MockitoExtension.class)
public class PathAcceptanceMockTest {

    @InjectMocks
    PathService pathService;

    @Test
    @DisplayName("경로 조회")
    void getPath() {

        경로_조회();

    }
}
