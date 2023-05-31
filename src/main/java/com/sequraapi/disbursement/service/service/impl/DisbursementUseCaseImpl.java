package com.sequraapi.disbursement.service.service.impl;

import com.sequraapi.disbursement.service.controller.reponse.DisbursementReportsResponse;
import com.sequraapi.disbursement.service.controller.reponse.SimpleOrderResponse;
import com.sequraapi.disbursement.service.controller.request.OrderRequest;
import com.sequraapi.disbursement.service.entity.OrderEntity;
import com.sequraapi.disbursement.service.entity.MerchantEntity;
import com.sequraapi.disbursement.service.entity.DisbursementEntity;
import com.sequraapi.disbursement.service.enums.DisbursementFrequencyEnum;
import com.sequraapi.disbursement.service.enums.StatusMinimumFeeEnum;
import com.sequraapi.disbursement.service.enums.StatusEnum;
import com.sequraapi.disbursement.service.exception.MerchantNotFoundException;
import com.sequraapi.disbursement.service.mapper.DisbursementMapper;
import com.sequraapi.disbursement.service.mapper.OrderMapper;
import com.sequraapi.disbursement.service.repository.OrderRepository;
import com.sequraapi.disbursement.service.repository.MerchantRepository;
import com.sequraapi.disbursement.service.repository.DisbursementRepository;
import com.sequraapi.disbursement.service.service.DisbursementUseCase;
import com.sequraapi.disbursement.service.service.FeeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisbursementUseCaseImpl implements DisbursementUseCase {

    private final MerchantRepository merchantRepository;
    private final OrderRepository orderRepository;
    private final FeeSmallerThenFiftyUseCaseImpl feeSmallerThenFiftyUseCase;
    private final FeeGreaterThenThreeHundredUseCaseImpl feeGreaterThenThreeHundredUseCase;
    private final FeeBetweenFiftyAndThreeHundredUseCaseImpl feeBetweenFiftyAndThreeHundredUseCase;

    private final DisbursementRepository disbursementRepository;
    private FeeUseCase feeUseCase;

    public void processDisbursementDaily(){

        List<MerchantEntity> merchants  = new ArrayList<>(merchantRepository
                .findByDisbursementFrequency(DisbursementFrequencyEnum.DAILY));

        List<OrderEntity> updatedOrders = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (MerchantEntity merchant: merchants){
            log.info("Starting process disbursement to merchant: {}", merchant.getId());
            List<OrderEntity> processOrders = orderRepository
                    .findByMerchantIdAndStatus(merchant.getId(), StatusEnum.PENDING);

            String paymentReference = UUID.randomUUID().toString();

            processOrders.forEach(order ->{
                setFeeUseCase(order);
                feeUseCase.setPaymentReference(paymentReference);
                feeUseCase.updateOrder(order);
            });
            calculateMinimumMonthlyFee(processOrders, merchant);
            updatedOrders.addAll(processOrders);
            log.info("Processed {} disbursement for merchant {}", processOrders.size(), merchant.getId());
        }

        log.info("Start updating the disbursement on database.");

        orderRepository.saveAll(updatedOrders);

        log.info("disbursement updated.");

        long endTime = System.currentTimeMillis();

        log.info("processed in {} seconds", (endTime - startTime)/1000);
    }

    public void processDisbursementWeekly(){

        List<MerchantEntity> merchants  = merchantRepository
                .findByDisbursementFrequency(DisbursementFrequencyEnum.WEEKLY).stream()
                .filter(m -> m.getLiveOn().getDayOfWeek().equals(LocalDate.now().getDayOfWeek()))
                .collect(Collectors.toList());

        List<OrderEntity> updatedOrders = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (MerchantEntity merchant: merchants){
            log.info("Starting process disbursement to merchant: {}", merchant.getId());
            List<OrderEntity> processOrders = orderRepository
                    .findByMerchantIdAndStatus(merchant.getId(), StatusEnum.PENDING);

            String paymentReference = UUID.randomUUID().toString();

            processOrders.forEach(order ->{
                setFeeUseCase(order);
                feeUseCase.setPaymentReference(paymentReference);
                feeUseCase.updateOrder(order);
            });

            calculateMinimumMonthlyFee(processOrders, merchant);
            updatedOrders.addAll(processOrders);
            log.info("Processed {} disbursement for merchant {}", processOrders.size(), merchant.getId());
        }

        log.info("Start updating the disbursement on database.");

        orderRepository.saveAll(updatedOrders);

        log.info("disbursement updated.");

        long endTime = System.currentTimeMillis();

        log.info("processed in {} seconds", (endTime - startTime)/1000);

    }

    public void processMinimumMonthlyFee() {
        List<DisbursementEntity> disbursements = disbursementRepository.findByStatusMinimumFee(StatusMinimumFeeEnum.PENDING);

        log.info("Starting process minimum monthly fee ...");
        disbursements.forEach(d->{
            Optional<MerchantEntity> maybeMerchant = merchantRepository.findById(d.getMerchantId());

            var merchant = maybeMerchant.orElseThrow(MerchantNotFoundException::new);
            verifyMinimumFeeChargeable(d, merchant.getMinimumMonthlyFee());
            d.setStatusMinimumFee(StatusMinimumFeeEnum.CHARGED);
        });

        log.info("Start updating the minimum monthly fee calculated on database.");

        disbursementRepository.saveAll(disbursements);

        log.info("Processing finished!");
    }

    private void calculateMinimumMonthlyFee(List<OrderEntity> processDisbursement, MerchantEntity merchant) {
        var map = processDisbursement
                .stream().collect(Collectors.groupingBy(d -> YearMonth.from(d.getCreatedAt())));

        map.forEach((ym, orderList) -> {
            var disbursementEntity = disbursementRepository
                    .findByYearAndMonthAndMerchantId(ym.getYear(), ym.getMonthValue(), merchant.getId());
            disbursementEntity.ifPresentOrElse(disbursement -> {
                if (disbursement.getStatusMinimumFee().equals(StatusMinimumFeeEnum.PENDING)) {
                    DisbursementMapper.INSTANCE.mapUpdatedDisbursement(disbursement, orderList, merchant);
                    verifyMinimumFeeChargeable(disbursement, merchant.getMinimumMonthlyFee());

                    disbursementRepository.save(disbursement);
                }
            }, () -> {
                var newDisbursement = DisbursementMapper.INSTANCE.mapNewDisbursement(orderList, merchant, ym);
                verifyMinimumFeeChargeable(newDisbursement, merchant.getMinimumMonthlyFee());

                disbursementRepository.save(newDisbursement);
            });
        });
    }

    private void verifyMinimumFeeChargeable(DisbursementEntity disbursement, BigDecimal minimumFee){
        if(disbursement.getTotalFee().compareTo(minimumFee) < 0){
            disbursement.setIsMinimumFeeChargeable(Boolean.TRUE);
            disbursement.setAmountFeeChargeable(minimumFee.subtract(disbursement.getTotalFee())
                    .setScale(2, RoundingMode.HALF_EVEN));
        } else {
            disbursement.setIsMinimumFeeChargeable(Boolean.FALSE);
            disbursement.setAmountFeeChargeable(BigDecimal.ZERO);
        }
    }


    private void setFeeUseCase(OrderEntity disbursement){
        var amount = disbursement.getAmount();

        if (amount.compareTo(BigDecimal.valueOf(50)) < 0){
            feeUseCase = feeSmallerThenFiftyUseCase;
        } else if (amount.compareTo(BigDecimal.valueOf(50)) > 0 &&
                amount.compareTo(BigDecimal.valueOf(300)) < 0) {
            feeUseCase = feeBetweenFiftyAndThreeHundredUseCase;
        } else {
            feeUseCase = feeGreaterThenThreeHundredUseCase;
        }
    }

    @Override
    public List<DisbursementReportsResponse> listReports() {

        List<DisbursementEntity> disbursements = disbursementRepository.findAll();

        var mapPerYear = disbursements.stream().collect(
                Collectors.groupingBy(DisbursementEntity::getYear));

        List<DisbursementReportsResponse> responses = new ArrayList<>();

        mapPerYear.forEach((year, disbursementList) ->
                responses.add(DisbursementMapper.INSTANCE.map(disbursementList, year)));

        return responses;
    }

    public SimpleOrderResponse createOrder(OrderRequest orderRequest){
      merchantRepository.findById(orderRequest.getMerchantId())
                .orElseThrow(MerchantNotFoundException::new);

        var orderSaved = orderRepository.save(OrderMapper.INSTANCE.map(orderRequest));

        return new SimpleOrderResponse(orderSaved.getId(), "Order created!");
    }
}
