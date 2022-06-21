package com.unitylife.demo.dao;

import com.unitylife.demo.entity.DBPost;
import com.unitylife.demo.entity.Post;

import java.util.Collection;

public interface PostDao {
    void createPost(Post post);

    void updatePosts(int id, Post post);

    void removePostsById(int id1, int id);

    DBPost getPostsById(int id);

    Collection<DBPost> getAllPosts();

    Collection<DBPost> getPostsFromGroup(int groupId);

    Collection<DBPost> getPostsFromGroup(String name);

    Collection<DBPost> getPostsByUser(int userId);

    Collection<DBPost> getPostsByUserFromGroup(int id1, int id2, int groupId);

    Collection<DBPost> getPostsByUserFromGroupName(int id1, int id2, String name);

    Collection<DBPost> getPostsByContent(String content);

    Collection<DBPost> getPostsByAuthor(String authorFirs, String authorLast);

    Collection<DBPost> getPostsByLikes(int likes);

    Collection<DBPost> getPostsByLikedBy(int likedBy);

    Collection<DBPost> getPostsByTime(int time);

    Collection<DBPost> getPostUserMainPage(int userid);

    Collection<DBPost> getPostsByWall(int wall);
}