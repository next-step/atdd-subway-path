package atdd.station.controller;

import atdd.station.domain.Edge;
import atdd.station.dto.EdgeDto;
import atdd.station.service.EdgeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping(value = "/edges", produces = "application/json")
public class EdgeController
{
    private static final String BASE_EDGE_URL = "/edges";

    @Resource(name = "edgeService")
    private EdgeService edgeService;

    @RequestMapping("")
    public ResponseEntity<Edge> createEdges(@RequestBody EdgeDto edgeDto)
    {
        Edge createEdge = edgeService.create(edgeDto);
        return ResponseEntity.created(URI.create(BASE_EDGE_URL + "/" + createEdge.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(createEdge);
    }
}
