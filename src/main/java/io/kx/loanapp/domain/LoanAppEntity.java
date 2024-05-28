package io.kx.loanapp.domain;

import com.google.protobuf.Empty;
import com.google.protobuf.util.Timestamps;
import io.kx.loanapp.api.LoanAppApi;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity.Effect;
import kalix.javasdk.eventsourcedentity.EventSourcedEntityContext;

// This class was initially generated based on the .proto definition by Kalix tooling.
// This is the implementation for the Event Sourced Entity Service described in your io/kx/loanapp/api/loan_app_api.proto file.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class LoanAppEntity extends AbstractLoanAppEntity {

  @SuppressWarnings("unused")
  private final String entityId;

  public LoanAppEntity(EventSourcedEntityContext context) {
    this.entityId = context.entityId();
  }

  @Override
  public LoanAppDomain.LoanAppDomainState emptyState() {
    return LoanAppDomain.LoanAppDomainState.getDefaultInstance();
  }

  @Override
  public Effect<Empty> submit(LoanAppDomain.LoanAppDomainState currentState, LoanAppApi.SubmitCommand submitCommand) {
    if(currentState.equals(LoanAppDomain.LoanAppDomainState.getDefaultInstance())) {
      //validate submit
      // generate and emit an event
      LoanAppDomain.Submitted submittedEvent = LoanAppDomain.Submitted.newBuilder()
              .setLoanAppId(submitCommand.getLoanAppId())
              .setClientId(submitCommand.getClientId())
              .setClientMonthlyIncomeCents(submitCommand.getClientMonthlyIncomeCents())
              .setLoanDurationMonths(submitCommand.getLoanDurationMonths())
              .setLoanAmountCents(submitCommand.getLoanAmountCents())
              .setEventGeneratedTimestamp(Timestamps.fromMillis(System.currentTimeMillis()))
              .build();
      return effects().emitEvent(submittedEvent).thenReply(any -> Empty.getDefaultInstance()); //persisting back to the journal handled by kalix
    } else if (currentState.getStatus() == LoanAppDomain.LoanAppDomainStatus.STATUS_IN_REVIEW) {
      return effects().reply(Empty.getDefaultInstance());
    } else {
      return effects().error("Something wrong during Submit");
    }
  }

  @Override
  public Effect<LoanAppApi.LoanAppState> get(LoanAppDomain.LoanAppDomainState currentState, LoanAppApi.GetCommand getCommand) {

    LoanAppApi.LoanAppState apiState = LoanAppApi.LoanAppState.newBuilder()
            .setClientId(currentState.getClientId())
            .setClientMonthlyIncomeCents(currentState.getClientMonthlyIncomeCents())
            .setLoanAmountCents(currentState.getLoanAmountCents())
            .setLoanDurationMonths(currentState.getLoanDurationMonths())
            .setStatus(LoanAppApi.LoanAppStatus.forNumber(currentState.getStatus().getNumber()))
            .setDeclineReason(currentState.getDeclineReason())
            .setLastUpdateTimestamp(currentState.getLastUpdateTimestamp())
            .build();
    return effects().reply(apiState);
  }

  @Override
  public Effect<Empty> approve(LoanAppDomain.LoanAppDomainState currentState, LoanAppApi.ApproveCommand approveCommand) {
    if(currentState.equals(LoanAppDomain.LoanAppDomainState.getDefaultInstance())) {
      return effects().error("Loan application not found");
    } else if(currentState.getStatus() == LoanAppDomain.LoanAppDomainStatus.STATUS_IN_REVIEW) {
      LoanAppDomain.Approved approvedEvent = LoanAppDomain.Approved.newBuilder()
              .setLoanAppId(approveCommand.getLoanAppId())
              .setEventGeneratedTimestamp(Timestamps.fromMillis(System.currentTimeMillis()))
              .build();
      return effects().emitEvent(approvedEvent).thenReply(any -> Empty.getDefaultInstance());
    } else if(currentState.getStatus() == LoanAppDomain.LoanAppDomainStatus.STATUS_APPROVED) {
      return effects().reply(Empty.getDefaultInstance());
    } else {
      return effects().error("Probably sth wrong, internal server error");
    }
  }

  @Override
  public Effect<Empty> decline(LoanAppDomain.LoanAppDomainState currentState, LoanAppApi.DeclineCommand declineCommand) {
    if(currentState.equals(LoanAppDomain.LoanAppDomainState.getDefaultInstance())) {
      return effects().error("Loan application not found");
    } else if(currentState.getStatus() == LoanAppDomain.LoanAppDomainStatus.STATUS_IN_REVIEW) {
      LoanAppDomain.Declined declinedEvent = LoanAppDomain.Declined.newBuilder()
              .setLoanAppId(declineCommand.getLoanAppId())
              .setEventGeneratedTimestamp(Timestamps.fromMillis(System.currentTimeMillis()))
              .setReason(declineCommand.getDeclineReasonMsg())
              .build();
      return effects().emitEvent(declinedEvent).deleteEntity().thenReply(any -> Empty.getDefaultInstance());
    } else if(currentState.getStatus() == LoanAppDomain.LoanAppDomainStatus.STATUS_DECLINED) {
      return effects().reply(Empty.getDefaultInstance());
    } else {
      return effects().error("Probably sth wrong, internal server error");
    }
  }

  @Override
  public LoanAppDomain.LoanAppDomainState submitted(LoanAppDomain.LoanAppDomainState currentState, LoanAppDomain.Submitted submitted) {
    return LoanAppDomain.LoanAppDomainState.newBuilder()
            .setClientId(submitted.getClientId())
            .setClientMonthlyIncomeCents(submitted.getClientMonthlyIncomeCents())
            .setLoanAmountCents(submitted.getLoanAmountCents())
            .setLoanDurationMonths(submitted.getLoanDurationMonths())
            .setStatus(LoanAppDomain.LoanAppDomainStatus.STATUS_IN_REVIEW)
            .setLastUpdateTimestamp(submitted.getEventGeneratedTimestamp())
            .build();
  }
  @Override
  public LoanAppDomain.LoanAppDomainState approved(LoanAppDomain.LoanAppDomainState currentState, LoanAppDomain.Approved approved) {
    return currentState.toBuilder()
            .setStatus(LoanAppDomain.LoanAppDomainStatus.STATUS_APPROVED)
            .setLastUpdateTimestamp(approved.getEventGeneratedTimestamp())
            .build();
  }
  @Override
  public LoanAppDomain.LoanAppDomainState declined(LoanAppDomain.LoanAppDomainState currentState, LoanAppDomain.Declined declined) {
    return currentState.toBuilder()
            .setStatus(LoanAppDomain.LoanAppDomainStatus.STATUS_DECLINED)
            .setLastUpdateTimestamp(declined.getEventGeneratedTimestamp())
            .setDeclineReason(declined.getReason())
            .build();
  }

}
