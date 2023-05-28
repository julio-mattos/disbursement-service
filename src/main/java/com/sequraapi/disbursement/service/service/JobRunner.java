package com.sequraapi.disbursement.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class JobRunner {

    public final DisbursementUseCase disbursementUseCase;

    @Scheduled(cron = "")
    public void processDaily(){
        disbursementUseCase.processDisbursementDaily();
    }

    @Scheduled(cron = "")
    public void processWeekly(){
        disbursementUseCase.processDisbursementWeekly();
    }

    @Scheduled(cron = "")
    public void processMinimumMonthlyFee(){
        disbursementUseCase.processMinimumMonthlyFee();
    }
}
