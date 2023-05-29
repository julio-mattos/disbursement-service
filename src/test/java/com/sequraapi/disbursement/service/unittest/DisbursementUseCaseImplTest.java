package com.sequraapi.disbursement.service.unittest;

import com.sequraapi.disbursement.service.TestHelper;
import com.sequraapi.disbursement.service.controller.reponse.DisbursementReportsResponse;
import com.sequraapi.disbursement.service.controller.reponse.SimpleOrderResponse;
import com.sequraapi.disbursement.service.controller.request.OrderRequest;
import com.sequraapi.disbursement.service.entity.DisbursementEntity;
import com.sequraapi.disbursement.service.entity.MerchantEntity;
import com.sequraapi.disbursement.service.entity.OrderEntity;
import com.sequraapi.disbursement.service.enums.DisbursementFrequencyEnum;
import com.sequraapi.disbursement.service.enums.StatusEnum;
import com.sequraapi.disbursement.service.enums.StatusMinimumFeeEnum;
import com.sequraapi.disbursement.service.repository.DisbursementRepository;
import com.sequraapi.disbursement.service.repository.MerchantRepository;
import com.sequraapi.disbursement.service.repository.OrderRepository;
import com.sequraapi.disbursement.service.service.impl.DisbursementUseCaseImpl;
import com.sequraapi.disbursement.service.service.impl.FeeBetweenFiftyAndThreeHundredUseCaseImpl;
import com.sequraapi.disbursement.service.service.impl.FeeGreaterThenThreeHundredUseCaseImpl;
import com.sequraapi.disbursement.service.service.impl.FeeSmallerThenFiftyUseCaseImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DisbursementUseCaseImplTest {

    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private OrderRepository orderRepository;

    @Spy
    private FeeSmallerThenFiftyUseCaseImpl feeSmallerThenFiftyUseCase;

    @Spy
    private FeeBetweenFiftyAndThreeHundredUseCaseImpl feeBetweenFiftyAndThreeHundredUseCase;

    @Spy
    private FeeGreaterThenThreeHundredUseCaseImpl feeGreaterThenThreeHundredUseCase;

    @Mock
    private DisbursementRepository disbursementRepository;

    @InjectMocks
    private DisbursementUseCaseImpl disbursementUseCase;

    @Test
    void shouldProcessOrdersDaily(){

        doReturn(TestHelper.getMerchantDaily()).when(merchantRepository)
                .findByDisbursementFrequency(DisbursementFrequencyEnum.DAILY);

        doReturn(TestHelper.getOrdersDaily()).when(orderRepository).findByMerchantIdAndStatus(anyString(), any(StatusEnum.class));

        doReturn(Optional.empty()).when(disbursementRepository).findByYearAndMonthAndMerchantId(anyInt(), anyInt(), anyString());

        disbursementUseCase.processDisbursementDaily();

        ArgumentCaptor<List<OrderEntity>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(orderRepository, times(1)).saveAll(argumentCaptor.capture());

        List<OrderEntity> orderEntities = argumentCaptor.getValue();

        Assertions.assertEquals(100, orderEntities.size());

    }

    @Test
    void shouldProcessOrdersWeekly(){
        List<MerchantEntity>  merchant = TestHelper.getMerchantWeekly();
        merchant.forEach(m->m.setLiveOn(LocalDate.now()));

        doReturn(merchant).when(merchantRepository)
                .findByDisbursementFrequency(DisbursementFrequencyEnum.WEEKLY);

        doReturn(TestHelper.getOrdersWeekly()).when(orderRepository).findByMerchantIdAndStatus(anyString(), any(StatusEnum.class));

        doReturn(Optional.empty()).when(disbursementRepository).findByYearAndMonthAndMerchantId(anyInt(), anyInt(), anyString());

        disbursementUseCase.processDisbursementWeekly();

        ArgumentCaptor<List<OrderEntity>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(orderRepository, times(1)).saveAll(argumentCaptor.capture());

        List<OrderEntity> orderEntities = argumentCaptor.getValue();

        Assertions.assertEquals(100, orderEntities.size());
    }

    @Test
    void shouldProcessMinimumMonthlyFee(){
        doReturn(TestHelper.getDisbursementList()).when(disbursementRepository).findByStatusMinimumFee(StatusMinimumFeeEnum.PENDING);

        doReturn(Optional.of(TestHelper.getOneMerchantDaily())).when(merchantRepository).findById(TestHelper.getOneMerchantDaily().getId());
        doReturn(Optional.of(TestHelper.getOneMerchantWeekly())).when(merchantRepository).findById(TestHelper.getOneMerchantWeekly().getId());

        disbursementUseCase.processMinimumMonthlyFee();

        ArgumentCaptor<List<DisbursementEntity>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(disbursementRepository, times(1)).saveAll(argumentCaptor.capture());

        List<DisbursementEntity> disbursementEntities = argumentCaptor.getValue();

        Assertions.assertEquals(10, disbursementEntities.size());

    }

    @Test
    void shouldReturnListOfReports(){

        doReturn(TestHelper.getDisbursementList()).when(disbursementRepository).findAll();
        List<DisbursementReportsResponse> responses = disbursementUseCase.listReports();
        Assertions.assertEquals(2, responses.size());
    }

    @Test
    void shouldCreateOrder(){
        doReturn(Optional.of(TestHelper.getOneMerchantDaily())).when(merchantRepository).findById(TestHelper.getOneMerchantDaily().getId());
        doReturn(OrderEntity.builder().id("12345").createdAt(LocalDate.now()).amount(BigDecimal.valueOf(100)).status(StatusEnum.PENDING).build())
                .when(orderRepository).save(any(OrderEntity.class));

        OrderRequest orderRequest = new OrderRequest(TestHelper.getOneMerchantDaily().getId(), BigDecimal.valueOf(100));

        SimpleOrderResponse response = disbursementUseCase.createOrder(orderRequest);

        Assertions.assertEquals(response.getId(), "12345");
        Assertions.assertEquals(response.getMessage(), "Order created!");

    }
}
