package krilovs.andrejs.app.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import krilovs.andrejs.app.exception.ApplicationException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProfileController.class)
class ProfileControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ProfileService profileService;

  @Autowired
  private ObjectMapper objectMapper;

  private final ProfileDto profileDto = ProfileDto.builder()
    .userId(1L)
    .firstName("Andrejs")
    .lastName("Krilovs")
    .personalCode("10092025-12345")
    .birthDate(LocalDate.of(2025, 9, 10))
    .phone("+37121234567")
    .bankCardNumber("1234 5678 9012 3456")
    .build();

  @Test
  void shouldReturnUserProfile() throws Exception {
    Mockito.when(profileService.selectProfile(1L)).thenReturn(profileDto);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/loan-app/profiles/{id}", 1L))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1L))
      .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Andrejs"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Krilovs"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.personalCode").value("10092025-12345"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value("2025-09-10"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("+37121234567"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.bankCardNumber").value("1234 5678 9012 3456"));
  }

  @Test
  void shouldReturnErrorWhenNoProfile() throws Exception {
    Mockito.when(profileService.selectProfile(1L))
      .thenThrow(new ApplicationException(HttpStatus.NOT_FOUND, "Profile not found for current user"));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/loan-app/profiles/{id}", 1L))
      .andExpect(MockMvcResultMatchers.status().isNotFound())
      .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.containsString("Profile not found for current user")));
  }

  @Test
  void shouldUpdateUserProfile() throws Exception {
    Mockito.when(profileService.saveProfile(Mockito.any(ProfileDto.class))).thenReturn(profileDto);

    mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/loan-app/profiles")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(profileDto)))
      .andExpect(MockMvcResultMatchers.status().isAccepted())
      .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1L))
      .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Andrejs"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Krilovs"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.personalCode").value("10092025-12345"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value("2025-09-10"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("+37121234567"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.bankCardNumber").value("1234 5678 9012 3456"));
  }

  @Test
  void shouldNotUpdateProfileWhenUserNotFound() throws Exception {
    Mockito.when(profileService.saveProfile(Mockito.any(ProfileDto.class)))
      .thenThrow(new ApplicationException(HttpStatus.NOT_FOUND, "User not found"));

    mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/loan-app/profiles")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(profileDto)))
      .andExpect(MockMvcResultMatchers.status().isNotFound())
      .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.containsString("User not found")));
  }
}