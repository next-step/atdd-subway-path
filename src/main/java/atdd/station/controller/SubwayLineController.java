package atdd.station.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/subway-lines",
        produces = "application/json")
public class SubwayLineController {

    @PostMapping("/")
    public ResponseEntity<String> create(@RequestBody String subwayLine) {
        return ResponseEntity
                .created(URI.create("/subway-lines/1"))
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
}
