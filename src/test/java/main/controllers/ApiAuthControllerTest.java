package main.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.api.requests.LoginRequest;
import main.api.requests.UserRequest;
import main.model.Captcha;
import main.model.User;
import main.model.util.Role;
import main.repositories.CaptchaRepository;
import main.servises.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApiAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CaptchaRepository captchaRepository;

    @Value("${time.expired.mc}")
    private long timeExpired;

    private static final String existingEmail = "dan.shipilov@gmail.com";
    private String userName = "daniil";

    @Test
    public void checkTest() throws Exception {
        this.mockMvc.perform(get("/api/auth/check"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{'result': false}"));
    }

    @Test
    @WithUserDetails(existingEmail)
    public void checkWithUserTest() throws Exception {
        this.mockMvc.perform(get("/api/auth/check"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value(userName));
    }

    @Test
    public void rightLoginTest() throws Exception {

        LoginRequest request = new LoginRequest(existingEmail, "123456");
        this.mockMvc.perform(
                        post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated().withAuthorities(Role.USER.getAuthorities()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value(userName));
    }

    @Test
    public void wrongLoginTest() throws Exception {

        LoginRequest request = new LoginRequest(existingEmail, "wrongPassword");
        this.mockMvc.perform(
                        post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(existingEmail)
    public void logoutTest() throws Exception {
        this.mockMvc.perform(get("/api/auth/logout"))
                .andDo(print())
                .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    public void registerBadRequestTest() throws Exception {

        UserRequest request = new UserRequest();
        request.setName("newUser");
        request.setEmail(existingEmail);
        request.setPassword("123");
        request.setCaptcha("1");
        request.setCaptchaSecret("1");

        this.mockMvc.perform(
                            post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("false"))
                .andExpect(jsonPath("$.errors.captcha").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    @Sql(statements = "delete from users where users.email = 'new@email.com'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void registerTest() throws Exception {

        String email = "new@email.com";
        List<Captcha> captchaList = (List<Captcha>) captchaRepository.findAll();
        Captcha captcha = captchaList.stream().findFirst().get();

        UserRequest request = new UserRequest();
        request.setName("newUser");
        request.setEmail(email);
        request.setPassword("123456");
        request.setCaptcha(captcha.getCode());
        request.setCaptchaSecret(captcha.getSecretCode());

        this.mockMvc.perform(
                        post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));

        User user = userService.getUserByEmail(email);
        Assert.assertEquals(email, user.getEmail());
    }

    @Test
    public void captchaTest() throws Exception {

        this.mockMvc.perform(get("/api/auth/captcha"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secret").exists())
                .andExpect(jsonPath("$.image").exists());

        long expiredTime = new Date().getTime() - timeExpired;
        List<Captcha> captchaList = (List<Captcha>) captchaRepository.findAll();
        long countOldCaptcha = captchaList.stream().filter(captcha -> captcha.getTime().getTime() < expiredTime).count();

        Assert.assertEquals(0, countOldCaptcha);
    }

    @Test
    public void restoreTest() throws Exception {

        UserRequest request = new UserRequest();
        request.setEmail(existingEmail);

        this.mockMvc.perform(
                        post("/api/auth/restore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    public void changePasswordWithBadRequestTest() throws Exception {

        UserRequest request = new UserRequest();
        request.setCode("wrongCode");
        request.setPassword("123");
        request.setCaptcha("wrongCaptcha");
        request.setCaptchaSecret("wrongCaptchaSecret");

        this.mockMvc.perform(post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("false"))
                .andExpect(jsonPath("$.errors.code").exists())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(jsonPath("$.errors.captcha").exists());
    }

    @Test
    public void changePasswordTest() throws Exception {

        userService.sendRestoreEmail(existingEmail);
        User user = userService.getUserByEmail(existingEmail);
        List<Captcha> captchaList = (List<Captcha>) captchaRepository.findAll();
        Captcha captcha = captchaList.stream().findFirst().get();

        UserRequest request = new UserRequest();
        request.setCode(user.getCode());
        request.setPassword("123456");
        request.setCaptcha(captcha.getCode());
        request.setCaptchaSecret(captcha.getSecretCode());

        this.mockMvc.perform(post("/api/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }

}
