package atdd.station.web;

import atdd.station.domain.station.Station;
import atdd.station.domain.station.StationRepository;
import atdd.station.web.dto.StationListResponseDto;
import atdd.station.web.dto.StationRequestDto;
import atdd.station.web.dto.StationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
@RestController
public class StationController {
    @Autowired
    StationRepository stationRepository;

    @PostMapping("/stations")
    public @ResponseBody ResponseEntity stations(@RequestBody String inputJson) {
        Logger logger = Logger.getLogger("station");
        logger.info(inputJson);

        HttpHeaders responseHeaders = new HttpHeaders();

        responseHeaders.set("Content-Type", "application/json");

        URI location = URI.create("/stations");
        return ResponseEntity.created(location)
                .headers(responseHeaders)
                .body(inputJson);
    }

    @PostMapping("createStation")
    public ResponseEntity createStation(@RequestBody StationRequestDto stationRequestDto) {
        Logger logger = Logger.getLogger("createStation");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        URI location = URI.create("/createStation");

        Long id = stationRepository.save(Station.builder().name(stationRequestDto.getName()).build()).getId();

        Station entity = stationRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("해당역이존재하지않습니다."));
        StationResponseDto stationResponseDto = new StationResponseDto(entity);

        logger.info("저장된 아이디 : " + id);
        logger.info("저장해서 뽑아낸값 : " + stationResponseDto.getName());
        logger.info("저장해서 뽑아낸값 : " + stationResponseDto.getId());

        return ResponseEntity.created(location)
                .header(String.valueOf(responseHeaders))
                .body(stationResponseDto);
    }

    @GetMapping("selectStationList")
    public ResponseEntity selectStation(){
        Logger logger = Logger.getLogger("selectStation");

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        URI location = URI.create("/selectStationList");

        //given
        stationRepository.save(Station.builder().name("강남역").build());
        stationRepository.save(Station.builder().name("수서역").build());
        //when
        List<Station> stationList = stationRepository.findAll();
        logger.info("1 :" + stationList.get(0).getId() + "name :" + stationList.get(0).getName());
        logger.info("2 : "+ stationList.get(1).getId() + "name :" + stationList.get(1).getName());

        //StationResponseDto stationResponseDto = StationResponseDto.builder().name(stationList.get(0).getName()).build();

        //then
        return ResponseEntity.created(location)
        .header(String.valueOf(responseHeaders))
        .body(stationList);
    }

}
