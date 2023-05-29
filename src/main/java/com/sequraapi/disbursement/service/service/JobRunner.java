package com.sequraapi.disbursement.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JobRunner {

    public final DisbursementUseCase disbursementUseCase;

    @Scheduled(cron = "${cron.daily}")
    public void processDaily(){
        disbursementUseCase.processDisbursementDaily();
    }

    @Scheduled(cron = "${cron.daily}")
    public void processWeekly(){
        disbursementUseCase.processDisbursementWeekly();
    }

    @Scheduled(cron = "${cron.monthly}")
    public void processMinimumMonthlyFee(){
        disbursementUseCase.processMinimumMonthlyFee();
    }
}
