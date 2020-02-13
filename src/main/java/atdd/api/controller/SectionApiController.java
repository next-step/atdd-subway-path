package atdd.api.controller;

import atdd.domain.stations.Section;
import atdd.serivce.stations.SectionService;
import atdd.web.dto.section.SectionCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/section/")
public class SectionApiController {
    private SectionService sectionService;

    @PostMapping
    public ResponseEntity<Section> create(@RequestBody SectionCreateRequestDto requestDto){
        Section section=sectionService.create(requestDto);
        return ResponseEntity
                .created(URI.create("/section/"+section.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(section);
    }
}
