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
            log.info("Starting process disbursement to merchant: {}", merchant);
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
            log.info("Processed {} disbursement for merchant {}", processOrders.size(), merchant);
        }

        log.info("Start updating the disbursement on database.");

        orderRepository.saveAll(updatedOrders);

        log.info("disbursement updated.");

        long endTime = System.currentTimeMillis();

        log.info("processed in {} seconds", (endTime - startTime)/1000);

    }

    public void processMinimumMonthlyFee() {
        List<DisbursementEntity> disbursements = disbursementRepository.findByStatusMinimumFee(StatusMinimumFeeEnum.PENDING);

        disbursements.forEach(d->{
            Optional<MerchantEntity> maybeMerchant = merchantRepository.findById(d.getMerchantId());

            var merchant = maybeMerchant.orElseThrow(MerchantNotFoundException::new);
            verifyMinimumFeeChargeable(d, merchant.getMinimumMonthlyFee());
            d.setStatusMinimumFee(StatusMinimumFeeEnum.CHARGED);
        });

        disbursementRepository.saveAll(disbursements);
    }

    private void calculateMinimumMonthlyFee(List<OrderEntity> processDisbursement, MerchantEntity merchant) {
        var map= processDisbursement
                .stream().collect(Collectors.groupingBy(d -> YearMonth.from(d.getCreatedAt())));

        map.forEach((ym, d) -> {
            var disbursementEntity = disbursementRepository
                    .findByYearAndMonthAndMerchantId(ym.getYear(), ym.getMonthValue(), merchant.getId());
            disbursementEntity.ifPresentOrElse(disbursement -> {
                if (disbursement.getStatusMinimumFee().equals(StatusMinimumFeeEnum.PENDING)){
                    var newTotalFee = d.stream().map(OrderEntity::getFeeAmount).reduce(disbursement.getTotalFee(), BigDecimal::add);
                    var newTotalAmount =   d.stream().map(OrderEntity::getAmount).reduce(disbursement.getTotalAmount(), BigDecimal::add);
                    var totalDisbursement = d.stream().map(m-> 1).reduce(disbursement.getTotalDisbursement(), Integer::sum);

                    disbursement.setTotalDisbursement(totalDisbursement);
                    disbursement.setTotalFee(newTotalFee.setScale(2, RoundingMode.HALF_EVEN));
                    disbursement.setTotalAmount(newTotalAmount.setScale(2, RoundingMode.HALF_EVEN));

                    verifyMinimumFeeChargeable(disbursement, merchant.getMinimumMonthlyFee());

                    disbursementRepository.save(disbursement);
                }
            }, () -> {
                var newDisbursement = new DisbursementEntity();

                var totalFee = d.stream().map(OrderEntity::getFeeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                var totalAmount =   d.stream().map(OrderEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                var totalDisbursement = d.stream().map(m-> 1).reduce(0, Integer::sum);

                newDisbursement.setTotalFee(totalFee.setScale(2, RoundingMode.HALF_EVEN));
                newDisbursement.setMerchantId(merchant.getId());
                newDisbursement.setTotalDisbursement(totalDisbursement);
                newDisbursement.setTotalAmount(totalAmount);
                newDisbursement.setYear(ym.getYear());
                newDisbursement.setMonth(ym.getMonthValue());
                newDisbursement.setStatusMinimumFee(StatusMinimumFeeEnum.PENDING);

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

        var mapPerYear = disbursements.stream().collect(Collectors.groupingBy(DisbursementEntity::getYear));

        List<DisbursementReportsResponse> responses = new ArrayList<>();

        mapPerYear.forEach((y, d) -> {
            DisbursementReportsResponse response = new DisbursementReportsResponse();

            var numberOfDisbursement = d.stream().map(DisbursementEntity::getTotalDisbursement).reduce(0, Integer::sum);
            var amountDisbursedToMerchants = d.stream().map(DisbursementEntity::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            var amountOfOrdersFees = d.stream().map(DisbursementEntity::getTotalFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            var monthlyFeesCharged = d.stream().map(m -> m.getIsMinimumFeeChargeable() ? 1 : 0).reduce(0, Integer::sum);
            var amountMonthlyFeeCharged = d.stream().map(DisbursementEntity::getAmountFeeChargeable).reduce(BigDecimal.ZERO, BigDecimal::add);

            response.setYear(y);
            response.setNumberOfDisbursements(numberOfDisbursement);
            response.setAmountDisbursedToMerchants(amountDisbursedToMerchants);
            response.setAmountOfOrdersFees(amountOfOrdersFees);
            response.setMonthlyFeesCharged(monthlyFeesCharged);
            response.setAmountMonthlyFeeCharged(amountMonthlyFeeCharged);

            responses.add(response);
        });
        return responses;
    }

    public SimpleOrderResponse createOrder(OrderRequest orderRequest){
      merchantRepository.findById(orderRequest.getMerchantId())
                .orElseThrow(MerchantNotFoundException::new);

        var orderSaved = orderRepository.save(OrderMapper.INSTANCE.map(orderRequest));

        return new SimpleOrderResponse(orderSaved.getId(), "Order created!");
    }
}
