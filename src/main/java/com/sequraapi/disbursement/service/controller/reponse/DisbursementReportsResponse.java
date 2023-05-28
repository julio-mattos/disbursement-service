package com.sequraapi.disbursement.service.controller.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DisbursementReportsResponse {

    private int year;
    private int numberOfDisbursements;
    private BigDecimal amountDisbursedToMerchants;
    private BigDecimal amountOfOrdersFees;
    private int monthlyFeesCharged;
    private BigDecimal amountMonthlyFeeCharged;

}
