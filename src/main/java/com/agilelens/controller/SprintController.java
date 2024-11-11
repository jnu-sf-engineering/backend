package com.agilelens.controller;

import com.agilelens.dto.SprintCreateRequest;
import com.agilelens.dto.SprintCreateResponse;
import com.agilelens.dto.SprintFindResponse;
import com.agilelens.dto.SprintUpdateRequest;
import com.agilelens.global.CommonResponse;
import com.agilelens.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/v1/sprints")
@RestController
public class SprintController {
    private final SprintService sprintService;

    @PostMapping()
    public CommonResponse<SprintCreateResponse> create(@RequestBody SprintCreateRequest request) {

        SprintCreateResponse response = SprintCreateResponse
                .from(sprintService.save(request));
        return CommonResponse.success(response);
    };

    @GetMapping("/{sprint_id}")
    public CommonResponse<SprintFindResponse> read(@PathVariable("sprint_id") Long sprint_id) {
        SprintFindResponse response = SprintFindResponse
                .from(sprintService.findById(sprint_id));
        return CommonResponse.success(response);
    }

    @PutMapping("/{sprint_id}")
    public CommonResponse<SprintCreateResponse> update(@PathVariable("sprint_id") Long sprint_id,
                                       @RequestBody SprintUpdateRequest request) {
        SprintCreateResponse response = SprintCreateResponse
                .from(sprintService.update(sprint_id, request));
        return CommonResponse.success();
    }

    @DeleteMapping("/{sprint_id}")
    public CommonResponse<Void> delete(@PathVariable("sprint_id") Long sprint_id) {
        sprintService.delete(sprint_id);
        return CommonResponse.success();
    }
}
