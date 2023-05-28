package com.sequraapi.disbursement.service.service;

import com.sequraapi.disbursement.service.controller.reponse.DisbursementReportsResponse;
import com.sequraapi.disbursement.service.controller.reponse.SimpleOrderResponse;
import com.sequraapi.disbursement.service.controller.request.OrderRequest;

import java.util.List;

public interface DisbursementUseCase {

    void processDisbursementDaily();

    void processDisbursementWeekly();

    void processMinimumMonthlyFee();

    List<DisbursementReportsResponse> listReports();

    SimpleOrderResponse createOrder(OrderRequest orderRequest);
}
