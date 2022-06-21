package com.unitylife.demo.config;

import com.unitylife.demo.entity.User;
import com.unitylife.demo.helperMethods.UserInformation;
import com.unitylife.demo.service.AuthenticationService;
import com.unitylife.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private UserService userService;

  @Autowired
  private AuthenticationService authService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse,
                                      Authentication authentication)
      throws IOException, ServletException {
    String email = new UserInformation().getEmail();
    User user = userService.getUserByEmail(email);
    String password = user.getPassword();
    int userid = user.getId();
    String token = authService.getToken();
    String create_session_sql = "INSERT INTO log_info (userid, email, password, token) " +
        "SELECT ?, ?, ?, ? " +
        "WHERE NOT EXISTS (SELECT * FROM log_info WHERE userid = ?)";
    jdbcTemplate.update(create_session_sql, userid, email, password, token, userid);
    super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
  }
}