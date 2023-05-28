package com.sequraapi.disbursement.service.entity;

import com.sequraapi.disbursement.service.enums.DisbursementFrequencyEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@Document(collection = "merchant")
public class MerchantEntity {

    @Id
    @Field("_id")
    private String id;
    private String email;
    @Field(name = "live_on")
    private LocalDate liveOn;
    @Field(name = "disbursement_frequency")
    private DisbursementFrequencyEnum disbursementFrequency;
    @Field(name = "minimum_monthly_fee" )
    private BigDecimal minimumMonthlyFee;

}
