package main.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import main.dto.responses.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Service
public class UserLogoutHandler implements LogoutHandler {

    public UserLogoutHandler() {
    }

    @SneakyThrows
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();

        LoginResponse loginResponse = new LoginResponse(true, null);
        ResponseEntity<LoginResponse> responseEntity = ResponseEntity.ok(loginResponse);

        String s = new ObjectMapper().writeValueAsString(responseEntity);

        writer.write(s);
        writer.flush();
        writer.close();
    }
}
