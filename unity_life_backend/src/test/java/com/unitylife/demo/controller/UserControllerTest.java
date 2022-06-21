package com.unitylife.demo.controller;

import com.unitylife.demo.entity.User;
import com.unitylife.demo.service.UserService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Created by bryan on 6/21/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @MockBean
  private UserService userService;
  @Autowired
  private MockMvc mockMvc;


  @Test
  public void getUserById() throws Exception {
    User user = new User();
    user.setId(1);
    user.setFirstName("a");
    user.setLastName("a");
    user.setEmail("a");
    user.setAge(1);
    user.setGender("a");
    user.setCountry("a");
    user.setCity("a");
    user.setPassword("a");

    Mockito.doReturn(user).when(userService).getUserById(1);
    Assert.assertEquals(user, userService.getUserById(1));
  }
}