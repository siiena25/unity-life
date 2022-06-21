package com.unitylife.demo.service;

import com.unitylife.demo.dao_impl.PostgreSQLGroupDaoImpl;
import com.unitylife.demo.entity.Group;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 6/21/2017.
 */
public class GroupServiceTest {
  private GroupService groupService;
  private PostgreSQLGroupDaoImpl postgreSQLGroupDao;

  @Before
  public void setUp() throws Exception {
    postgreSQLGroupDao = Mockito.mock(PostgreSQLGroupDaoImpl.class);
    groupService = new GroupService(postgreSQLGroupDao);
  }

 @Test
  public void GroupTests() throws Exception {
   Group group1 = new Group();
   group1.setGroupID(1);
   group1.setAdmin("1");
   group1.setName("a");
   Group group2 = new Group();
   group2.setGroupID(2);
   group2.setAdmin("2");
   group2.setName("b");

   List<Group> allGroups = new ArrayList<>();
   allGroups.add(group1);
   allGroups.add(group2);

   List<Group> groupOneList = new ArrayList<>();
   groupOneList.add(group1);

   List<Group> groupTwoList = new ArrayList<>();
   groupTwoList.add(group2);

   Mockito.doReturn(group1).when(postgreSQLGroupDao).getGroupById(1);
   Assert.assertEquals(group1, postgreSQLGroupDao.getGroupById(1));

//   Mockito.doReturn(groupOneList).when(postgreSQLGroupDao).getGroupByAdmin(1);
//   Assert.assertEquals(groupOneList, postgreSQLGroupDao.getGroupByAdmin(1));

   Mockito.doReturn(groupTwoList).when(postgreSQLGroupDao).getGroupByAdmin("b");
   Assert.assertEquals(groupTwoList, postgreSQLGroupDao.getGroupByAdmin("b"));

   Mockito.doReturn(groupOneList).when(postgreSQLGroupDao).getGroupByName("a");
   Assert.assertEquals(groupOneList, postgreSQLGroupDao.getGroupByName("a"));

   Mockito.doReturn(allGroups).when(postgreSQLGroupDao).getAllGroup();
   Assert.assertEquals(allGroups, postgreSQLGroupDao.getAllGroup());

   Mockito.doReturn(groupOneList).when(postgreSQLGroupDao).getAllGroupsForUser(1);
   Assert.assertEquals(groupOneList, postgreSQLGroupDao.getAllGroupsForUser(1));

 }

}