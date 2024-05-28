package io.kx.loanapp.domain;

import com.google.protobuf.Empty;
import io.kx.loanapp.api.LoanAppApi;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity;
import kalix.javasdk.eventsourcedentity.EventSourcedEntityContext;
import kalix.javasdk.testkit.EventSourcedResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// This class was initially generated based on the .proto definition by Kalix tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class LoanAppEntityTest {

  @Test
  public void submitTest() {
    LoanAppEntityTestKit service = LoanAppEntityTestKit.of(LoanAppEntity::new);
    // SubmitCommand command = SubmitCommand.newBuilder()...build();
    // EventSourcedResult<Empty> result = service.submit(command);
    String uuid = UUID.randomUUID().toString();
    LoanAppApi.SubmitCommand submitCmd = LoanAppApi.SubmitCommand.newBuilder()
            .setLoanAppId(uuid)
            .setClientId("Peter-client-id-1")
            .setClientMonthlyIncomeCents(1000000)
            .setLoanAmountCents(20000000)
            .setLoanDurationMonths(45)
            .build();
    EventSourcedResult<Empty> result = service.submit(submitCmd);
    assertTrue(result.didEmitEvents());
    LoanAppApi.GetCommand getCmd = LoanAppApi.GetCommand.newBuilder()
            .setLoanAppId(uuid).build();
    EventSourcedResult<LoanAppApi.LoanAppState> getResult = service.get(getCmd);
    assertEquals(LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW, getResult.getReply().getStatus());

  }


  @Test
  @Disabled("to be implemented")
  public void getTest() {
    LoanAppEntityTestKit service = LoanAppEntityTestKit.of(LoanAppEntity::new);
    // GetCommand command = GetCommand.newBuilder()...build();
    // EventSourcedResult<LoanAppState> result = service.get(command);
  }


  @Test
  public void approveTest() {
    LoanAppEntityTestKit service = LoanAppEntityTestKit.of(LoanAppEntity::new);
    // ApproveCommand command = ApproveCommand.newBuilder()...build();
    // EventSourcedResult<Empty> result = service.approve(command);
    String uuid = UUID.randomUUID().toString();
    LoanAppApi.SubmitCommand submitCmd = LoanAppApi.SubmitCommand.newBuilder()
            .setLoanAppId(uuid)
            .setClientId("Peter-client-id-1")
            .setClientMonthlyIncomeCents(1000000)
            .setLoanAmountCents(20000000)
            .setLoanDurationMonths(45)
            .build();
    EventSourcedResult<Empty> result = service.submit(submitCmd);
    assertTrue(result.didEmitEvents());
    LoanAppApi.GetCommand getCmd = LoanAppApi.GetCommand.newBuilder()
            .setLoanAppId(uuid).build();
    EventSourcedResult<LoanAppApi.LoanAppState> getResult = service.get(getCmd);
    assertEquals(LoanAppApi.LoanAppStatus.STATUS_IN_REVIEW, getResult.getReply().getStatus());

    LoanAppApi.ApproveCommand approveCommand = LoanAppApi.ApproveCommand.newBuilder()
            .setLoanAppId(uuid).build();
    EventSourcedResult<Empty> approveResult = service.approve(approveCommand);
    assertTrue(approveResult.didEmitEvents());

    EventSourcedResult<LoanAppApi.LoanAppState> getApprovedResult = service.get(getCmd);
    assertEquals(LoanAppApi.LoanAppStatus.STATUS_APPROVED, getApprovedResult.getReply().getStatus());
  }


  @Test
  @Disabled("to be implemented")
  public void declineTest() {
    LoanAppEntityTestKit service = LoanAppEntityTestKit.of(LoanAppEntity::new);
    // DeclineCommand command = DeclineCommand.newBuilder()...build();
    // EventSourcedResult<Empty> result = service.decline(command);
  }

}
