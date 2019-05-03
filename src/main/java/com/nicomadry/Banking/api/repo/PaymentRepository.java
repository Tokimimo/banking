package com.nicomadry.Banking.api.repo;

import com.nicomadry.Banking.api.data.dto.PaymentDTO;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.api.data.entity.Payment;
import com.nicomadry.Banking.api.data.enums.OrderingType;
import com.nicomadry.Banking.api.data.enums.PaymentStatus;
import com.nicomadry.Banking.api.service.BankAccountService;
import com.nicomadry.Banking.itl.exception.NotCreatableException;
import com.nicomadry.Banking.itl.exception.NotDeletableException;
import com.nicomadry.Banking.itl.exception.NotFoundException;
import com.nicomadry.Banking.itl.exception.NotUpdatableException;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Dependent
public class PaymentRepository {

  private static final String SELECT_ALL_PAYMENTS_DYNAMIC_ORDER_BY = "SELECT p from Payment p order by p.creationDate ";
  private static final String SELECT_PAYMENTS_WITH_BENEFICIARY_IN_PERIOD_WITH_DYNAMIC_ORDER_BY = "SELECT p from Payment p WHERE p.beneficiaryAccountNumber = :beneficiaryAccountNumber and p.creationDate >= :periodStart and p.creationDate <= :periodEnd order by p.creationDate ";

  private EntityManager entityManager;
  private BankAccountService bankAccountService;

  @Inject
  public void init( EntityManager entityManager, BankAccountService bankAccountService )
  {
    this.entityManager = entityManager;
    this.bankAccountService = bankAccountService;
  }

  /**
   * Tries to find the {@link Payment} for the given ID.
   *
   * @param id The ID of the {@link Payment}
   *
   * @return An {@link Optional} possibly containing the {@link Payment}
   */
  public Optional<Payment> findById( Long id )
  {
    requireNonNull( id, "The id must not be null" );

    return Optional.ofNullable( entityManager.find( Payment.class, id ) );
  }

  /**
   * Returns all {@link Payment Payments} in the system ordered by the given {@link OrderingType Ordering}.
   *
   * @param orderingType The {@link OrderingType} to decide between ASC and DESC ordering
   *
   * @return The list of all {@link Payment Payments}
   */
  public List<Payment> findAllOrderedByCreationDate( final OrderingType orderingType )
  {
    requireNonNull( orderingType, "The orderingType must not be null" );

    final TypedQuery<Payment> typedQuery = entityManager
            .createQuery( SELECT_ALL_PAYMENTS_DYNAMIC_ORDER_BY + orderingType.getName(), Payment.class );

    return typedQuery.getResultList();
  }

  /**
   * Returns all {@link Payment Payments} which have the given beneficiary IBAN and were made in the given period ordered by the {@link
   * OrderingType Ordering}.
   *
   * @param beneficiaryAccountNumber The IBAN of the beneficiary
   * @param periodStart              The {@link ZonedDateTime} representing the start of the period
   * @param periodEnd                The {@link ZonedDateTime} representing the end of the period
   * @param orderingType             The {@link OrderingType} to decide between ASC and DESC ordering
   *
   * @return The list of all {@link Payment Payments}
   */
  public List<Payment> findAllForBeneficiaryIbanAndPeriod( final String beneficiaryAccountNumber, final ZonedDateTime periodStart,
                                                           final ZonedDateTime periodEnd, final OrderingType orderingType )
  {
    requireNonNull( beneficiaryAccountNumber, "The beneficiaryAccountNumber must not be null" );
    requireNonNull( periodStart, "The periodStart must not be null" );
    requireNonNull( periodEnd, "The periodEnd must not be null" );
    requireNonNull( orderingType, "The orderingType must not be null" );


    final TypedQuery<Payment> typedQuery = entityManager.createQuery(
            SELECT_PAYMENTS_WITH_BENEFICIARY_IN_PERIOD_WITH_DYNAMIC_ORDER_BY + orderingType.getName(), Payment.class );

    typedQuery.setParameter( "beneficiaryAccountNumber", beneficiaryAccountNumber );
    typedQuery.setParameter( "periodStart", periodStart );
    typedQuery.setParameter( "periodEnd", periodEnd );

    return typedQuery.getResultList();
  }

  /**
   * Persists the information's of the {@link PaymentDTO} as a {@link Payment}.
   *
   * @param paymentDTO The {@link PaymentDTO} holding all information's to be persisted
   *
   * @return The persisted {@link Payment}
   *
   * @throws NotCreatableException If the {@link Payment} couldn't be created
   */
  public Payment createPayment( PaymentDTO paymentDTO ) throws NotCreatableException, NotFoundException
  {
    requireNonNull( paymentDTO, "The payment dto must not be null" );

    Payment payment = new Payment( paymentDTO );

    final Optional<BankAccount> bankAccountOptional = bankAccountService.getBankAccount( paymentDTO.getGiverAccount() );

    payment.setGiverAccount(
            bankAccountOptional.orElseThrow( () -> new NotFoundException( "No bank account found to associate the payment to" ) ) );
    payment.setStatus( PaymentStatus.EXECUTED );
    payment.setCreationDate( ZonedDateTime.now() );

    try {
      entityManager.persist( payment );
    }
    catch ( Exception e ) {
      throw new NotCreatableException( "The payment: " + payment.getAmount() + " couldn't be created",
                                       e );
    }

    return payment;
  }

  /**
   * This function throws a {@link NotUpdatableException}, since a {@link Payment} is not updateable.
   *
   * @param paymentId  The ID of the {@link Payment} to be updated
   * @param paymentDTO The {@link PaymentDTO} holding all new information's
   *
   * @return The updated {@link Payment}
   *
   * @throws NotUpdatableException Because a {@link Payment} cannot be updated
   */
  public Payment updatePayment( Long paymentId, PaymentDTO paymentDTO ) throws NotUpdatableException
  {
    throw new NotUpdatableException( "A payment cannot be updated" );
  }

  /**
   * This function throws a {@link NotUpdatableException}, since a {@link Payment} is not updateable.
   *
   * @param payment    The {@link Payment} to be updated
   * @param paymentDTO The {@link PaymentDTO} holding all new information's
   *
   * @return The updated {@link Payment}
   *
   * @throws NotUpdatableException Because a {@link Payment} cannot be updated
   */
  public Payment updatePayment( Payment payment, PaymentDTO paymentDTO ) throws NotUpdatableException
  {
    throw new NotUpdatableException( "A payment cannot be updated" );
  }

  /**
   * Deletes the given {@link Payment}.
   *
   * @param paymentId The ID of the {@link Payment} to be deleted
   *
   * @throws NotDeletableException If the {@link Payment} couldn't be deleted
   * @throws NotFoundException     If no {@link Payment} was found for the given ID
   */
  public void deletePayment( Long paymentId ) throws NotDeletableException, NotFoundException
  {
    requireNonNull( paymentId, "The payment id must not be null" );

    final Optional<Payment> paymentOptional = findById( paymentId );
    final Payment payment = paymentOptional.orElseThrow( () -> new NotFoundException( "No payment found for ID:" + paymentId ) );

    deletePayment( payment );
  }

  /**
   * Deletes the given {@link Payment}.
   *
   * @param payment The {@link Payment} to be deleted
   *
   * @throws NotDeletableException If the {@link Payment} couldn't be deleted
   */
  public void deletePayment( Payment payment ) throws NotDeletableException
  {
    requireNonNull( payment, "The payment must not be null" );

    try {
      entityManager.remove( payment );
    }
    catch ( Exception e ) {
      throw new NotDeletableException( "The payment: " + payment.getId() + "couldn't be deleted", e );
    }
  }
}
