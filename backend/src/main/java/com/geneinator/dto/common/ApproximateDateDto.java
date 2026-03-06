package com.geneinator.dto.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class ApproximateDateDto {
    private Integer year;
    private Integer month;
    private Integer day;
    private Boolean isApproximate;
    private String dateText;
}
