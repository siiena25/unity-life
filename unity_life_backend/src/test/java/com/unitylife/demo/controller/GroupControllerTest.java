package com.unitylife.demo.controller;

import com.unitylife.demo.entity.Group;
import com.unitylife.demo.service.GroupService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 6/21/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(GroupController.class)
public class GroupControllerTest {

  @MockBean
  private GroupService groupService;
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void getGroupByNameTest() throws Exception {

    Group mockGroup = new Group();
    mockGroup.setAdmin("1");
    mockGroup.setGroupID(2);
    mockGroup.setName("jafdls");

    Group mockGroup2 = new Group();
    mockGroup2.setAdmin("2");
    mockGroup2.setGroupID(3);
    mockGroup2.setName("fsadjkl");

    List<Group> list = new ArrayList<>();
    list.add(mockGroup);
    list.add(mockGroup2);

    Mockito.doReturn(list).when(groupService).getGroupByName("null");
    Assert.assertEquals(list, groupService.getGroupByName("null"));

  }




}