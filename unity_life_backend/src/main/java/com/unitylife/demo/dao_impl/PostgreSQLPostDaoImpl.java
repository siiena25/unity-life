package com.unitylife.demo.dao_impl;

import com.unitylife.demo.dao.PostDao;
import com.unitylife.demo.entity.DBPost;
import com.unitylife.demo.entity.Group;
import com.unitylife.demo.entity.Post;
import com.unitylife.demo.entity.User;
import com.unitylife.demo.exceptions.AuthenticationException;
import com.unitylife.demo.exceptions.ResourceNotFoundException;
import com.unitylife.demo.row_mappers.UserRowMapper;
import com.unitylife.demo.service.FriendsService;
import com.unitylife.demo.service.GroupService;
import com.unitylife.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Repository("PostgresPostRepo")
public class PostgreSQLPostDaoImpl implements PostDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FriendsService friendsService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Override
    public Collection<DBPost> getAllPosts() {
        final String sql = "SELECT * FROM posts";
        List<DBPost> DBposts = jdbcTemplate.query(sql, new PostRowMapper());
        return DBposts;
    }

    @Override
    public Collection<DBPost> getPostsByUser(int userId) {
        final String sql1 = "SELECT * FROM users WHERE userid = ? ";
        User user = jdbcTemplate.queryForObject(sql1, new UserRowMapper(), userId);
        final String sql2 = "SELECT * FROM posts WHERE authorfirst = ? AND authorlast = ? ORDER BY time";
        List<DBPost> posts = jdbcTemplate.query(sql2,
                new PostRowMapper(), user.getFirstName(), user.getLastName());
        return posts;
    }

    @Override
    public Collection<DBPost> getPostsByUserFromGroup(int id1, int id2, int groupId) {
        checkMember(id1, groupId);
        final String sql1 = "SELECT * FROM users WHERE userid = ? ";
        User user = jdbcTemplate.queryForObject(sql1, new UserRowMapper(), id2);
        String authorFirst = user.getFirstName();
        String authorLast = user.getLastName();
        final String sql2 = "SELECT * FROM posts WHERE authorfirst = ? AND authorlast = ?" +
                "AND visibility = ? ORDER BY time";
        List<DBPost> posts = jdbcTemplate.query(sql2, new PostRowMapper(),
                authorFirst, authorLast, groupId);
        return posts;
    }

    @Override
    public Collection<DBPost> getPostsByUserFromGroupName(int id1, int id2, String name) {
        int groupId = jdbcTemplate.queryForObject("SELECT groupid FROM groups WHERE groupname = ?", new Object[]{name}, Integer.class);
        return getPostsByUserFromGroup(id1, id2, groupId);
    }

    public void checkMember(int userId, int groupId) {
        final String sql = "SELECT memberid FROM membership WHERE memberid = ? AND groupid = ?";
        Integer result;
        try {
            result = jdbcTemplate.queryForObject(sql, new Object[]{userId, groupId}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }
        if (result == null) {
            throw new AuthenticationException(userId);
        }
    }

//  @Override
//  public Collection<Post> getPostsByUser(String firstName, int time) {
//    return null;
//  }

    @Override
    public DBPost getPostsById(int id) {
        final String sql = "SELECT * FROM posts WHERE id = ?";
        DBPost post = jdbcTemplate.queryForObject(sql, new PostRowMapper(), id);
        return post;
    }

    @Override
    public Collection<DBPost> getPostsFromGroup(int groupId) {
        final String sql = "SELECT * FROM posts WHERE visibility = ?";
        List<DBPost> posts = jdbcTemplate.query(sql, new PostRowMapper(), groupId);
        return posts;
    }

    @Override
    public Collection<DBPost> getPostsFromGroup(String name) {
        int groupId = jdbcTemplate.queryForObject("SELECT groupid FROM groups WHERE groupname = ?"
                , new Object[]{name}, Integer.class);
        final String sql = "SELECT * FROM posts WHERE visibility = ? ORDER BY time";
        List<DBPost> posts = jdbcTemplate.query(sql, new PostRowMapper(), groupId);
        return posts;
    }

    @Override
    public void removePostsById(int id1, int id) {
        checkAuthor(id1, id);
        final String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    //to see if the post(id) is written by the user(id1)
    public void checkAuthor(int id1, int id) {
        String result;
        String authorfirst = "yes";
        String authorlast = "yes";
        try {
            authorfirst = jdbcTemplate.queryForObject
                    ("SELECT authorfirst FROM posts WHERE id = ?",
                            new Object[]{id},
                            String.class);
            authorlast = jdbcTemplate.queryForObject
                    ("SELECT authorlast FROM posts WHERE id = ?",
                            new Object[]{id},
                            String.class);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(authorfirst, authorlast);
        }
        try {
            String sql = "SELECT * FROM users WHERE firstname = ? AND lastname = ?";
            result = jdbcTemplate.queryForObject(sql, new Object[]{authorfirst, authorlast}, String.class);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }
        if (result == null) {
            throw new AuthenticationException(id1);
        }
    }


    @Override
    public void updatePosts(int id, Post assignment) {
        checkAuthor(id, assignment.getId());
        int wall = userService.getUserByUserName(assignment.getWallName()).getId();
        DBPost dbpost = new DBPost(assignment.getAuthorFirstName(),
                assignment.getAuthorLastName(), assignment.getContent(),
                assignment.getLikes(), assignment.getTime(), assignment.getVisibility(), wall);
        final String sql = "UPDATE posts SET content = ?, likes = ?, " +
                "time = ?, visibility = ?, wall = ? WHERE id = ?";
        jdbcTemplate.update(sql, dbpost.getContent(), dbpost.getLikes()
                , dbpost.getTime(), dbpost.getVisibility(), wall, dbpost.getId());
    }

    @Override
    public void createPost(Post post) {
        //convert post intp DBPost
        int wall = userService.getUserByUserName(post.getWallName()).getId();
        DBPost dbpost = new DBPost(post.getAuthorFirstName(),
                post.getAuthorLastName(), post.getContent(),
                post.getLikes(), post.getTime(), post.getVisibility(), wall);
        //INSERT INTO table_name (column1, column2, column3,...)
        //VALUES (value1, value2, value3,...)
        final String sql = "INSERT INTO posts (authorfirst, authorlast, content, likes, time, visibility, wall) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, dbpost.getAuthorFirst(),
                dbpost.getAuthorLast(),
                dbpost.getContent(),
                dbpost.getLikes(),
                dbpost.getTime(),
                dbpost.getVisibility(),
                wall);
    }

    @Override
    public Collection<DBPost> getPostsByContent(String content) {
        final String sql = "SELECT * FROM posts WHERE content = ?";
        Collection<DBPost> posts = jdbcTemplate.query(sql, new PostRowMapper(), content);
        return posts;
    }

    @Override
    public Collection<DBPost> getPostsByAuthor(String authorFirst, String authorLast) {
        final String sql = "SELECT * FROM posts WHERE authorfirst = ? AND authorlast = ?";
        Collection<DBPost> posts = jdbcTemplate.query(sql, new PostRowMapper(),
                new Object[]{authorFirst, authorLast});
        return posts;
    }

    @Override
    public Collection<DBPost> getPostsByLikes(int likes) {
        final String sql = "SELECT * FROM posts WHERE likes = ?";
        Collection<DBPost> posts = jdbcTemplate.query(sql, new PostRowMapper(), likes);
        return posts;
    }

    @Override
    public Collection<DBPost> getPostsByLikedBy(int likedBy) {
        return null;
    }

    @Override
    public Collection<DBPost> getPostsByTime(int time) {
        final String sql = "SELECT * FROM posts WHERE time = ?";
        Collection<DBPost> posts = jdbcTemplate.query(sql, new PostRowMapper(), time);
        return posts;
    }

    @Override
    public Collection<DBPost> getPostsByWall(int wall) {
        final String sql = "SELECT * FROM posts WHERE wall = ?";
        Collection<DBPost> posts = jdbcTemplate.query(sql, new PostRowMapper(), wall);
        return posts;
    }

    @Override
    public Collection<DBPost> getPostUserMainPage(int userid) {
        Collection<DBPost> result = new ArrayList<>();
        Collection<User> friends = friendsService.getAllFriends(userid);
        for (User user : friends) {
            Collection<DBPost> posts = getPostsByUser(user.getId());
            result.addAll(posts);
        }
        Collection<Group> groups = groupService.getAllGroupsForUser(userid);
        for (Group group : groups) {
            Collection<DBPost> posts = getPostsFromGroup(group.getGroupID());
            result.addAll(posts);
        }
        Collection<DBPost> wallPosts = getPostsByWall(userid);
        result.addAll(wallPosts);
        return result;
    }


    private static class PostRowMapper implements RowMapper<DBPost> {

        @Override
        public DBPost mapRow(ResultSet resultSet, int i) throws SQLException {
            DBPost post = new DBPost();
            post.setId(resultSet.getInt("id"));
            post.setAuthorFirst(resultSet.getString("authorfirst"));
            post.setAuthorLast(resultSet.getString("authorlast"));
            post.setContent(resultSet.getString("content"));
            post.setLikes(resultSet.getInt("likes"));
            post.setTime(resultSet.getInt("time"));
            post.setVisibility(resultSet.getInt("visibility"));
            post.setWall(resultSet.getInt("wall"));
            return post;
        }
    }
}
