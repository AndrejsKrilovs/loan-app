package krilovs.andrejs.app.loan.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LoanApplicationController.class)
class LoanApplicationControllerTest {
  private final LoanApplicationDto dto = LoanApplicationDto.builder()
    .customerId(10L)
    .amount(BigDecimal.valueOf(1000))
    .termDays(Short.valueOf("30"))
    .percent(BigDecimal.TEN)
    .status(LoanApplicationStatus.APPROVED)
    .build();

  private final SimpleLoanApplicationDto simpleDto = SimpleLoanApplicationDto.builder()
    .customerId(10L)
    .amount(BigDecimal.valueOf(1000))
    .termDays(Short.valueOf("30"))
    .percent(BigDecimal.TEN)
    .status(LoanApplicationStatus.APPROVED)
    .build();

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LoanApplicationService loanApplicationService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldReturnLoanApplications() throws Exception {
    var dto = LoanApplicationDto.builder().id(1L).build();
    var serviceMethod = loanApplicationService.getLoanApplications(Mockito.anyMap());
    Mockito.when(serviceMethod).thenReturn(List.of(dto));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/loan-app/loan-applications")
        .param("status", "NEW"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
  }

  @Test
  void shouldCreateLoanApplications() throws Exception {
    var serviceMethod = loanApplicationService.createLoanApplication(Mockito.any(LoanApplicationDto.class));
    Mockito.when(serviceMethod).thenReturn(dto);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/loan-app/loan-applications")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(10))
      .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(1000))
      .andExpect(MockMvcResultMatchers.jsonPath("$.termDays").value(30))
      .andExpect(MockMvcResultMatchers.jsonPath("$.percent").value(10))
      .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("APPROVED"));
  }

  @Test
  void shouldChangeLoanApplicationStatus() throws Exception {
    var changeRequest = new ChangeStatusRequest(1L, LoanApplicationStatus.APPROVED);
    var serviceMethod = loanApplicationService.changeLoanApplicationStatus(
      Mockito.any(LoanApplicationStatus.class),
      Mockito.anyLong()
    );
    Mockito.when(serviceMethod).thenReturn(simpleDto);

    mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/loan-app/loan-applications")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(changeRequest)))
      .andExpect(MockMvcResultMatchers.status().isAccepted())
      .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(10))
      .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(1000))
      .andExpect(MockMvcResultMatchers.jsonPath("$.termDays").value(30))
      .andExpect(MockMvcResultMatchers.jsonPath("$.percent").value(10))
      .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("APPROVED"));
  }
}