package com.kfokam48.presencerelecture.web;

import com.kfokam48.presencerelecture.service.TableauService;
import com.kfokam48.presencerelecture.web.dto.LigneTableauResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TableauController {

    private final TableauService tableauService;

    public TableauController(TableauService tableauService) {
        this.tableauService = tableauService;
    }

    @GetMapping("/api/tableau")
    public List<LigneTableauResponse> consulter(@RequestParam Long promotionId) {
        return tableauService.consulter(promotionId);
    }
}
