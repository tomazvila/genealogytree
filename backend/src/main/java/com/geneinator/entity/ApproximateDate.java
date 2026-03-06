package com.geneinator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproximateDate {

    @Column(name = "date_year")
    private Integer year;

    @Column(name = "date_month")
    private Integer month;

    @Column(name = "date_day")
    private Integer day;

    @Column(name = "date_is_approximate")
    private Boolean isApproximate;

    @Column(name = "date_text")
    private String dateText;

    public static ApproximateDate fromYear(int year) {
        return ApproximateDate.builder()
                .year(year)
                .isApproximate(false)
                .build();
    }

    public static ApproximateDate approximate(int year) {
        return ApproximateDate.builder()
                .year(year)
                .isApproximate(true)
                .dateText("circa " + year)
                .build();
    }

    public static ApproximateDate fullDate(int year, int month, int day) {
        return ApproximateDate.builder()
                .year(year)
                .month(month)
                .day(day)
                .isApproximate(false)
                .build();
    }
}
