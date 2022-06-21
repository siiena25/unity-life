package com.unitylife.demo.dao;

import com.unitylife.demo.dao_impl.PostgreSQLGroupDaoImpl;
import com.unitylife.demo.entity.Group;
import com.unitylife.demo.service.GroupService;

import org.junit.Assert;
import org.junit.Before;
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
@WebMvcTest(PostgreSQLGroupDaoImpl.class)

public class PostgresSQLGroupDaoImplTest {
  @MockBean
  private GroupService groupService;
  @Autowired
  private MockMvc mockMvc;
  private PostgreSQLGroupDaoImpl postgresSQLGroupDao;
  private UserDao userDao;
  private GroupDao groupDao;

  @Before
  public void setUp() throws Exception {
    userDao = Mockito.mock(UserDao.class);
    groupDao = Mockito.mock(GroupDao.class);
    postgresSQLGroupDao = new PostgreSQLGroupDaoImpl();

  }

  @Test
  public void getGroupById() throws Exception {
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

    Assert.assertEquals(mockGroup, postgresSQLGroupDao.getGroupById(1));
    Mockito.doReturn(mockGroup).when(postgresSQLGroupDao).getGroupById(2);
    Assert.assertEquals(mockGroup, postgresSQLGroupDao.getGroupById(1));





  }

}