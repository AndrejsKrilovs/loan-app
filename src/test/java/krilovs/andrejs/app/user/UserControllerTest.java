package krilovs.andrejs.app.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import krilovs.andrejs.app.exception.ApplicationException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
@WebMvcTest(UserController.class)
class UserControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  private final UserDto userDto = UserDto.builder()
    .email("test@example.com")
    .password("s0Me%Str0ngPwd#")
    .role(UserRole.CUSTOMER)
    .enabled(Boolean.TRUE)
    .build();

  @Test
  void shouldReturnActiveUsersList() throws Exception {
    Mockito.when(userService.getActiveUsers()).thenReturn(List.of(userDto));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/loan-app/users"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.is(1)));
  }

  @Test
  void shouldReturnErrorWhenNoActiveUsers() throws Exception {
    Mockito.when(userService.getActiveUsers())
      .thenThrow(new ApplicationException(HttpStatus.OK, "No active users at this moment"));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/loan-app/users"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.containsString("No active users")));
  }

  @Test
  void shouldRegisteredUser() throws Exception {
    Mockito.when(userService.registerNewUser(Mockito.any(UserDto.class))).thenReturn(userDto);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/loan-app/users/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userDto)))
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("test@example.com")));
  }

  @Test
  void shouldNotRegisteredUserWhenEmailExists() throws Exception {
    Mockito.when(userService.registerNewUser(Mockito.any(UserDto.class)))
      .thenThrow(new ApplicationException(HttpStatus.CONFLICT, "already exists"));

    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/loan-app/users/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userDto)))
      .andExpect(MockMvcResultMatchers.status().isConflict())
      .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.containsString("already exists")));
  }

  @Test
  void shouldLoginUser() throws Exception {
    Mockito.when(userService.login(Mockito.any(UserDto.class))).thenReturn(userDto);

    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/loan-app/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userDto)))
      .andExpect(MockMvcResultMatchers.status().isAccepted())
      .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("test@example.com")));
  }

  @Test
  void shouldNotLoginUserWhenInvalidCredentials() throws Exception {
    Mockito.when(userService.login(Mockito.any(UserDto.class)))
      .thenThrow(new ApplicationException(HttpStatus.NOT_FOUND, "Invalid email"));

    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/loan-app/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userDto)))
      .andExpect(MockMvcResultMatchers.status().isNotFound())
      .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.containsString("Invalid email")));
  }
}