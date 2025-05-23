package com.example.repair.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/vehicle-repair")
    public ResponseEntity<Map<String, Object>> getVehicleRepairStatistics(
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate) {
        if (startDate == null) {
            startDate = Instant.now().minus(30, ChronoUnit.DAYS);
        }
        if (endDate == null) {
            endDate = Instant.now();
        }
        return ResponseEntity.ok(statisticsService.getVehicleRepairStatistics(startDate, endDate));
    }

    @GetMapping("/cost")
    public ResponseEntity<Map<String, Object>> getCostAnalysis(
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate) {
        if (startDate == null) {
            startDate = Instant.now().minus(30, ChronoUnit.DAYS);
        }
        if (endDate == null) {
            endDate = Instant.now();
        }
        return ResponseEntity.ok(statisticsService.getCostAnalysis(startDate, endDate));
    }

    @GetMapping("/feedback")
    public ResponseEntity<Map<String, Object>> getNegativeFeedbackStatistics() {
        return ResponseEntity.ok(statisticsService.getNegativeFeedbackStatistics());
    }

    @GetMapping("/unfinished")
    public ResponseEntity<Map<String, Object>> getUnfinishedTasksStatistics() {
        return ResponseEntity.ok(statisticsService.getUnfinishedTasksStatistics());
    }

    @GetMapping("/worker-tasks")
    public ResponseEntity<Map<String, Object>> getWorkerTasksStatistics(
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate) {
        if (startDate == null) {
            startDate = Instant.now().minus(30, ChronoUnit.DAYS);
        }
        if (endDate == null) {
            endDate = Instant.now();
        }
        return ResponseEntity.ok(statisticsService.getWorkerTasksStatistics(startDate, endDate));
    }

    @GetMapping("/worker-income")
    public ResponseEntity<Map<String, Object>> getWorkerIncomeStatistics(
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate) {
        if (startDate == null) {
            startDate = Instant.now().minus(30, ChronoUnit.DAYS);
        }
        if (endDate == null) {
            endDate = Instant.now();
        }
        return ResponseEntity.ok(statisticsService.getWorkerIncomeStatistics(startDate, endDate));
    }
}
