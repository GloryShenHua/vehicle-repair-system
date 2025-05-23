package com.example.repair.service;

import java.time.Instant;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getVehicleRepairStatistics(Instant startDate, Instant endDate);
    Map<String, Object> getCostAnalysis(Instant startDate, Instant endDate);
    Map<String, Object> getNegativeFeedbackStatistics();
    Map<String, Object> getUnfinishedTasksStatistics();
    Map<String, Object> getWorkerTasksStatistics(Instant startDate, Instant endDate);
    Map<String, Object> getWorkerIncomeStatistics(Instant startDate, Instant endDate);
}
