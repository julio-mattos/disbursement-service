package com.sequraapi.disbursement.service.controller;

import com.sequraapi.disbursement.service.controller.reponse.DisbursementReportsResponse;
import com.sequraapi.disbursement.service.controller.reponse.SimpleOrderResponse;
import com.sequraapi.disbursement.service.controller.request.OrderRequest;
import com.sequraapi.disbursement.service.service.DisbursementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/disbursement")
public class DisbursementController {

    private final DisbursementUseCase disbursementUseCase;

    @PostMapping("/process/weekly")
    public void weekly(){
        disbursementUseCase.processDisbursementWeekly();
    }


    @PostMapping("/process/daily")
    public void daily(){
        disbursementUseCase.processDisbursementDaily();
    }

    @PostMapping("/process/minimum-monthly-fee")
    public void processMinimumMonthlyFee(){
        disbursementUseCase.processMinimumMonthlyFee();
    }

    @GetMapping("/reports")
    public ResponseEntity<List<DisbursementReportsResponse>> listReports(){
       return ResponseEntity.ok(disbursementUseCase.listReports());
    }

    @PostMapping
    public ResponseEntity<SimpleOrderResponse> createOrder(@Validated @RequestBody OrderRequest orderRequest){
        SimpleOrderResponse response = disbursementUseCase.createOrder(orderRequest);
        return ResponseEntity.created(URI.create("/disbursement/".concat(response.getId())))
                .body(response);
    }
}
