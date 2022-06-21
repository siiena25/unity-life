package com.unitylife.demo.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectLogoutHandler implements LogoutSuccessHandler {
  @Override
  public void onLogoutSuccess(HttpServletRequest request,
                              HttpServletResponse response, Authentication authentication)
      throws IOException {
    String URL = "/login";
    response.sendRedirect(URL);
  }
}
