package com.portfolio.financas.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntryDTO {

    private Long id;

    private String description;

    private Integer month;

    private Integer year;

    private Long user;

    private BigDecimal value;

    private LocalDate registryDate;

    private String type;

    private String status;
}
