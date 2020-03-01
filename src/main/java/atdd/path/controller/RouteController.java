package atdd.path.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("routes")
public class RouteController {

    @GetMapping("distance")
    public ResponseEntity findShortestPath(@RequestParam("startId") Long startId, @RequestParam("endId") Long endId) {

        return ResponseEntity.ok().build();
    }
}
