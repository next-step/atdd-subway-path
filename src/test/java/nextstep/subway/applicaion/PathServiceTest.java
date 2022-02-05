package nextstep.subway.applicaion;

import nextstep.fake.LineFakeRepository;
import nextstep.fake.StationFakeRepository;
import nextstep.subway.applicaion.exception.NotFoundException;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathServiceTest {

    LineFakeRepository lineFakeRepository = new LineFakeRepository();
    StationRepository stationRepository = new StationFakeRepository();

    PathService pathService;

    @BeforeEach
    void setUp(){
        pathService = new PathService(lineFakeRepository,stationRepository);
    }

    @Test
    void 없는_역은_예외가_발생한다() {
        Assertions.assertThrows(NotFoundException.class,() -> {
            pathService.getPath(1L,2L);
        });
    }
}
