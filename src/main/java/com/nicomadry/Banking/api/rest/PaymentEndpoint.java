package com.nicomadry.Banking.api.rest;

import com.nicomadry.Banking.api.data.annotation.Authenticated;
import com.nicomadry.Banking.api.data.dto.BalanceDTO;
import com.nicomadry.Banking.api.data.dto.PaymentDTO;
import com.nicomadry.Banking.api.data.dto.PaymentFilterDTO;
import com.nicomadry.Banking.api.data.entity.Balance;
import com.nicomadry.Banking.api.data.entity.Payment;
import com.nicomadry.Banking.api.data.enums.OrderingType;
import com.nicomadry.Banking.api.repo.PaymentRepository;
import com.nicomadry.Banking.api.service.PaymentService;
import com.nicomadry.Banking.itl.exception.NotCreatableException;
import com.nicomadry.Banking.itl.exception.NotDeletableException;
import com.nicomadry.Banking.itl.exception.NotFoundException;
import com.nicomadry.Banking.itl.exception.ValidationException;
import com.nicomadry.Banking.itl.util.DateUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nicomadry.Banking.api.rest.PaymentEndpoint.PAYMENT_API_URI;
import static java.util.Objects.requireNonNull;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path( PAYMENT_API_URI )
@ApplicationScoped
@Transactional( value = REQUIRED, rollbackOn = Exception.class )
public class PaymentEndpoint {

  static final String PAYMENT_API_URI = "/payment";
  private static final String LIST_ALL_PAYMENTS_URI = "/list/{order}";
  private static final String LIST_PAYMENT_FOR_BENEFICIARY_IBAN_AND_PERIOD_URIT = "/listBeneficiary/{order}";
  private static final String DELETE_PAYMENT_URI = "/delete/{simpleId: [0-9]+}";
  private static final String PERFORM_TRANSACTION_URI = "/performTransaction/{accountId: [0-9]+}/{userId: [0-9]+}";
  private static final String DEPOSIT_MONEY_URI = "/deposit/{accountId: [0-9]}/{userId: [0-9]}";
  private static final String WITHDRAW_MONEY_URI = "/withdraw/{accountId: [0-9]}/{userId: [0-9]}";

  private PaymentService paymentService;
  private PaymentRepository paymentRepository;

  @Inject
  public void init( PaymentService paymentService, PaymentRepository paymentRepository )
  {
    this.paymentService = paymentService;
    this.paymentRepository = paymentRepository;
  }

  @GET
  @Authenticated
  @Path( LIST_ALL_PAYMENTS_URI )
  @Produces( APPLICATION_JSON )
  public Response listAllPayments( @PathParam( "order" ) OrderingType orderingType )
  {
    // TESTED AND WORKING
    final List<Payment> paymentList = paymentRepository.findAllOrderedByCreationDate( orderingType );
    final List<PaymentDTO> paymentDTOList = paymentList.stream().map( PaymentDTO::new ).collect( Collectors.toList() );

    return Response.ok( paymentDTOList ).build();
  }

  @POST
  @Authenticated
  @Path( LIST_PAYMENT_FOR_BENEFICIARY_IBAN_AND_PERIOD_URIT )
  @Consumes( APPLICATION_JSON )
  @Produces( APPLICATION_JSON )
  public Response listPaymentsForBeneficiaryAndPeriod( @PathParam( "order" ) OrderingType orderingType,
                                                       PaymentFilterDTO paymentFilterDTO ) throws ValidationException
  {
    // TESTED AND WORKING
    requireNonNull( paymentFilterDTO, "The paymentFilterDTO must not be null" );

    updatePaymentFilter( paymentFilterDTO );

    final List<Payment> paymentList = paymentRepository
            .findAllForBeneficiaryIbanAndPeriod( paymentFilterDTO.getBeneficiaryAccountNumber(), paymentFilterDTO.getPeriodStart(),
                                                 paymentFilterDTO.getPeriodEnd(), orderingType );
    final List<PaymentDTO> paymentDTOList = paymentList.stream().map( PaymentDTO::new ).collect( Collectors.toList() );

    return Response.ok( paymentDTOList ).build();
  }

  /**
   *
   * @param paymentFilterDTO
   * @throws ValidationException
   */
  private void updatePaymentFilter( PaymentFilterDTO paymentFilterDTO ) throws ValidationException
  {
    final Optional<ZonedDateTime> periodStart = DateUtils.stringToDateTime( paymentFilterDTO.getPeriodStartString() );

    paymentFilterDTO.setPeriodStart( periodStart.orElseThrow(
            () -> new ValidationException( "Couldn't parse period start: " + paymentFilterDTO.getPeriodStartString() ) ) );

    final Optional<ZonedDateTime> periodEnd = DateUtils.stringToDateTime( paymentFilterDTO.getPeriodEndString() );

    paymentFilterDTO.setPeriodEnd( periodEnd.orElseThrow(
            () -> new ValidationException( "Couldn't parse period end: " + paymentFilterDTO.getPeriodEndString() ) ) );
  }

  @DELETE
  @Authenticated
  @Path( DELETE_PAYMENT_URI )
  @Produces( APPLICATION_JSON )
  public Response deletePayment( @PathParam( "simpleId" ) Long paymentId ) throws NotFoundException, NotDeletableException
  {
    // TESTED AND WORKING
    paymentRepository.deletePayment( paymentId );

    return Response.ok().build();
  }

  @POST
  @Authenticated
  @Path( PERFORM_TRANSACTION_URI )
  @Consumes( APPLICATION_JSON )
  @Produces( APPLICATION_JSON )
  public Response performPayment( @PathParam( "accountId" ) Long accountId, @PathParam( "userId" ) Long userId,
                                  PaymentDTO transaction ) throws NotCreatableException, NotFoundException, ValidationException
  {
    // TESTED AND WORKING
    requireNonNull( transaction, "The transaction must not be null" );

    final Payment payment = paymentService.performTransaction( accountId, userId, transaction );

    return Response.ok( new PaymentDTO( payment ) ).build();
  }

  @POST
  @Authenticated
  @Path( DEPOSIT_MONEY_URI )
  @Consumes( APPLICATION_JSON )
  @Produces( APPLICATION_JSON )
  public Response depositMoney( @PathParam( "accountId" ) Long accountId, @PathParam( "userId" ) Long userId,
                                BalanceDTO depositBalance ) throws NotFoundException, NotCreatableException, ValidationException
  {
    // TESTED AND WORKING
    requireNonNull( depositBalance, "The depositBalance must not be null" );

    final Balance currentBalance = paymentService.depositMoney( accountId, userId, depositBalance );

    final BalanceDTO currentBalanceDto = new BalanceDTO( currentBalance );

    return Response.ok( currentBalanceDto ).build();
  }

  @POST
  @Authenticated
  @Path( WITHDRAW_MONEY_URI )
  @Consumes( APPLICATION_JSON )
  @Produces( APPLICATION_JSON )
  public Response withdrawMoney( @PathParam( "accountId" ) Long accountId, @PathParam( "userId" ) Long userId,
                                 BalanceDTO withdrawBalance ) throws NotFoundException, NotCreatableException, ValidationException
  {
    // TESTED AND WORKING
    requireNonNull( withdrawBalance, "The withdrawBalance must not be null" );

    final Balance currentBalance = paymentService.withdrawMoney( accountId, userId, withdrawBalance );

    final BalanceDTO currentBalanceDto = new BalanceDTO( currentBalance );

    return Response.ok( currentBalanceDto ).build();
  }

}
