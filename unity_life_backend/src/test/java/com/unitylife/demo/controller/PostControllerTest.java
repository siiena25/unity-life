package com.unitylife.demo.controller;

import com.unitylife.demo.entity.Post;
import com.unitylife.demo.service.PostService;

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
@WebMvcTest (PostController.class)
public class PostControllerTest {

  @MockBean
  private PostService postService;
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void postControllerTest() throws Exception {
    Post mockPost = new Post();
    mockPost.setId(1);
    mockPost.setAuthorFirstName("a");
    mockPost.setAuthorLastName("A");
    mockPost.setContent("a");
    mockPost.setLikes(1);
    mockPost.setTime(1);
    mockPost.setVisibility(1);

    Mockito.doReturn(mockPost).when(postService).getPostsById(1);
    Assert.assertEquals(mockPost, postService.getPostsById(1));


  }

}