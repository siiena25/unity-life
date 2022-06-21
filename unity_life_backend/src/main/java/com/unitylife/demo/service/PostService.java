package com.unitylife.demo.service;

import com.unitylife.demo.dao.PostDao;
import com.unitylife.demo.dao_impl.PostgreSQLPostDaoImpl;
import com.unitylife.demo.entity.DBPost;
import com.unitylife.demo.entity.Post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;


@Service
public class PostService {
    PostgreSQLPostDaoImpl postgreSQLPostDao;

    PostService(PostgreSQLPostDaoImpl postgreSQLPostDao) {
        this.postgreSQLPostDao = postgreSQLPostDao;
    }

    @Autowired
    @Qualifier("PostgresPostRepo")
    private PostDao postDao;

    public Collection<DBPost> getAllPosts() {
        return this.postDao.getAllPosts();
    }

    public Collection<DBPost> getPostsByUser(int userId) {
        return this.postDao.getPostsByUser(userId);
    }

    public Collection<DBPost> getPostsFromGroup(int groupId) {
        return this.postDao.getPostsFromGroup(groupId);
    }

    public Collection<DBPost> getPostsFromGroup(String name) {
        return this.postDao.getPostsFromGroup(name);
    }

    public Collection<DBPost> getPostsByUserFromGroup(int id1, int id2, int groupId) {
        return this.postDao.getPostsByUserFromGroup(id1, id2, groupId);
    }

    public DBPost getPostsById(int id) {
        return this.postDao.getPostsById(id);
    }

    public void removePostsById(int id1, int id) {
        this.postDao.removePostsById(id1, id);
    }

    public void updatePosts(int id, Post assignment) {
        this.postDao.updatePosts(id, assignment);
    }

    public Collection<DBPost> getPostUserMainPage(int userid) {
        return this.postDao.getPostUserMainPage(userid);
    }

    public void createPost(Post assignment) {
        this.postDao.createPost(assignment);
    }

    public Collection<DBPost> getPostsByContent(String content) {
        return this.postDao.getPostsByContent(content);
    }

    public Collection<DBPost> getPostsByUserFromGroupName(int id1, int id2, String name) {
        return this.postDao.getPostsByUserFromGroupName(id1, id2, name);
    }
}