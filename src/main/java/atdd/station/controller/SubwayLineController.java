package atdd.station.controller;

import atdd.station.domain.SubwayLine;
import atdd.station.service.StationService;
import atdd.station.service.SubwayLineService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping(value = "/subway-lines",
        produces = "application/json")
public class SubwayLineController {

    @Resource(name = "subwayLineService")
    private SubwayLineService subwayLineService;

    @PostMapping("/")
    public ResponseEntity<SubwayLine> create(@RequestBody SubwayLine subwayLine) {
        SubwayLine createdSubwayLine = subwayLineService.create(subwayLine);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/subway-lines/" + createdSubwayLine.getId()));
        return new ResponseEntity<>(createdSubwayLine, headers, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<String> list() {
        return ResponseEntity
                .ok()
                .body("[" +
                        "{" +
                        "    \"id\": 1," +
                        "    \"name\": \"5호선\"," +
                        "    \"startTime\": \"05:00\"," +
                        "    \"endTime\": \"23:50\"," +
                        "    \"interval\": \"10\"," +
                        "    \"stations\": [" +
                        "        {\"id\":4,\"name\":\"교대역\"}," +
                        "        {\"id\":5,\"name\":\"강남역\"}," +
                        "        {\"id\":2,\"name\":\"역삼역\"}," +
                        "        {\"id\":3,\"name\":\"선릉역\"}," +
                        "        {\"id\":1,\"name\":\"삼성역\"}" +
                        "    ]" +
                        " }" +
                        "{" +
                        "    \"id\": 2," +
                        "    \"name\": \"2호선\"," +
                        "    \"startTime\": \"05:00\"," +
                        "    \"endTime\": \"23:50\"," +
                        "    \"interval\": \"10\"," +
                        "    \"stations\": [" +
                        "        {\"id\":4,\"name\":\"교대역\"}," +
                        "        {\"id\":5,\"name\":\"강남역\"}," +
                        "        {\"id\":2,\"name\":\"역삼역\"}," +
                        "        {\"id\":3,\"name\":\"선릉역\"}," +
                        "        {\"id\":1,\"name\":\"삼성역\"}" +
                        "    ]" +
                        " }" +
                        "]");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> detail(@PathVariable int id) {
        return ResponseEntity
                .ok()
                .body("{" +
                        "    \"id\": 1," +
                        "    \"name\": \"2호선\"," +
                        "    \"startTime\": \"05:00\"," +
                        "    \"endTime\": \"23:50\"," +
                        "    \"interval\": \"10\"," +
                        "    \"stations\": [" +
                        "        {\"id\":4,\"name\":\"교대역\"}," +
                        "        {\"id\":5,\"name\":\"강남역\"}," +
                        "        {\"id\":2,\"name\":\"역삼역\"}," +
                        "        {\"id\":3,\"name\":\"선릉역\"}," +
                        "        {\"id\":1,\"name\":\"삼성역\"}" +
                        "    ]" +
                        " }");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        return ResponseEntity.ok().build();
    }
}
