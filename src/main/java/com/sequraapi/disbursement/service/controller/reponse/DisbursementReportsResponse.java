package com.sequraapi.disbursement.service.controller.reponse;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisbursementReportsResponse {

    private int year;
    private int numberOfDisbursements;
    private BigDecimal amountDisbursedToMerchants;
    private BigDecimal amountOfOrdersFees;
    private int monthlyFeesCharged;
    private BigDecimal amountMonthlyFeeCharged;

}
