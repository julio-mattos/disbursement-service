package com.sequraapi.disbursement.service.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Getter
@Setter
@Document("disbursement_report")
public class DisbursementReportEntity {

    @Id
    private int year;
    @Field("number_of_disbursements")
    private int numberOfDisbursements;
    @Field("amount_disbursed_to_merchants")
    private BigDecimal amountDisbursedToMerchants;
    @Field("amount_of_orders_fees")
    private BigDecimal amountOfOrdersFees;
    @Field("monthly_fees_charged")
    private int monthlyFeesCharged;
    @Field("amount_monthly_fee_charged")
    private BigDecimal amountMonthlyFeeCharged;

}
