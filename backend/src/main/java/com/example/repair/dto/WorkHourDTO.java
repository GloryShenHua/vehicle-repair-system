package com.example.repair.dto;
import java.math.BigDecimal;

public record WorkHourDTO(Long mechanicId,BigDecimal hours,BigDecimal hourlyRate){}
