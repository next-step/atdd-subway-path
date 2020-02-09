package atdd.station.controller;

import atdd.station.Service.StationService;
import atdd.station.domain.Station;
import atdd.station.domain.dto.StationDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/*
    step1 : 지하철역 관리 API 만들기
 */
@RestController
@RequestMapping(value = "/stations", produces = "application/json")
public class StationController
{
    @Resource(name = "stationService")
    private StationService stationService;
    /*
        1. 지하철역 등록
     */
    @PostMapping("/create")
    public ResponseEntity<Station> createStations(@RequestBody StationDto stationDto)
    {
        Station createdStation = stationService.create(stationDto);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/stations/list" + createdStation.getId()));
        ResponseEntity<Station> returnEntity = new ResponseEntity<>(createdStation, headers, HttpStatus.CREATED);
        return returnEntity;
    }
    /*
        2. 지하철역 목록 조회
     */
    @GetMapping("/list")
    public ResponseEntity list()
    {
        List<Station> stationList = stationService.list();
        ResponseEntity returnEntity = new ResponseEntity(stationList, HttpStatus.OK);
        return returnEntity;
    }
    /*
        3. 지하철역 조회
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity detailById(@PathVariable long id)
    {
        Optional<Station> detailStation = stationService.findById(id);
        ResponseEntity returnEntity = new ResponseEntity(detailStation, HttpStatus.OK);
        return returnEntity;
    }
    /*
        4. 지하철역 삭제
     */
    @DeleteMapping("/delete/{id}")
    public void deleteStation(@PathVariable long id)
    {
        stationService.deleteStationById(id);
    }
}
