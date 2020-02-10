package atdd.station.domain.station;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.logging.Logger;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class StationRepositoryTest {
    Logger logger = Logger.getLogger("select : ");

    @Autowired
    StationRepository stationRepository;

    @Test
    public void 조회() {
        //given
        String name = "강남역";
        stationRepository.save(Station.builder().name(name).build());

        //when
        List<Station> stationList = stationRepository.findAll();

        //then
        Station station = stationList.get(0);
        assertThat(station.getName()).isEqualTo(name);

        logger.info("id : " + String.valueOf(station.getId()));
        logger.info(station.getName());



    }
}
