package com.zh.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.zh.dao.*;
import com.zh.domain.*;
import com.zh.service.PostService;
import com.zh.utils.JsonUtils;
import com.zh.utils.JwtUtils;
import com.zh.utils.LegalUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    PostContentImagesTempDao postContentImagesTempDao;

    @Autowired
    PostContentImagesDao postContentImagesDao;

    @Autowired
    PostContentDao postContentDao;

    @Autowired
    PostDao postDao;

    @Autowired
    UserDao userDao;

    @Autowired
    UserHeadPortraitDao userHeadPortraitDao;

    @Autowired
    PostLikesDao postLikesDao;

    @Autowired
    PostRemarkDao postRemarkDao;

    @Override
    public ResponseResult<Object> uploadPostContentImages(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseResult<>(ResponseResult.Error, "上传图片为空", null);
        }
        //获取请求信息
        String token = request.getHeader("token");
        Claims claims;
        try {
            claims = JwtUtils.parseJwtToken(token);
        } catch (ExpiredJwtException e){
            return new ResponseResult<>(ResponseResult.TokenOutdated,"token已过期",null);
        }
        String user_id = claims.getId();

        try {
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            //判断文件是否合法
            if(LegalUtils.isImageFileName(originalFilename)!=null) return LegalUtils.isImageFileName(originalFilename);
            //构建文件路径
            String userDir = System.getProperty("user.dir"); // 获取当前工作目录
            String accessPath = null;//构建能够访问的路径
            if (originalFilename != null) {
                accessPath = "/post/images/" + LegalUtils.buildAccessPath(Integer.parseInt(user_id),originalFilename);
            }
            String relativePath = null;// 构建相对路径，不包含 JAR 文件的信息
            if (originalFilename != null) {
                relativePath = "data/post/images/"+ LegalUtils.buildAccessPath(Integer.parseInt(user_id),originalFilename);
            }
            String absolutePath = Paths.get(userDir, relativePath).toString(); // 将当前工作目录与相对路径结合，创建绝对路径
            System.out.println("postUploadFile Absolute Path: " + absolutePath);
            System.out.println("PostImageAccessPath: "+accessPath);

            // 保存文件到服务器
            // 检查文件是否存在，如果不存在则创建
            File savedfile = new File(absolutePath);
            if (!savedfile.exists()) {
                if (savedfile.createNewFile()) {
                    System.out.println("File created successfully.");
                } else {
                    System.out.println("Failed to create file.");
                }
            }
            file.transferTo(savedfile);
            //构建临时上传图像
            PostContentImagesTemp contentImagesTempSaved = new PostContentImagesTemp();
            contentImagesTempSaved.setUrl(accessPath);
            contentImagesTempSaved.setUserId(Integer.parseInt(user_id));
            //查询order
            List<PostContentImagesTemp> contentImagesTemp1 = postContentImagesTempDao.selectByUserIdOrderByImage_orderDescInt(Integer.parseInt(user_id));
            if(contentImagesTemp1.isEmpty()){
                contentImagesTempSaved.setImageOrder(1);
            }else{
                System.out.println(contentImagesTemp1);
                contentImagesTempSaved.setImageOrder(contentImagesTemp1.get(0).getImageOrder()+1);
            }
            try {
                postContentImagesTempDao.insert(contentImagesTempSaved);
            }catch (Exception e ){
                e.printStackTrace();
                return new ResponseResult<>(ResponseResult.Error, "服务器?出错", null);
            }

            System.out.println(contentImagesTempSaved);
            return new ResponseResult<>(ResponseResult.AccessOk,"上传成功",null);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseResult<>(ResponseResult.Error, "服务器出错", null);
        }

    }

    @Override
    public ResponseResult<Object> acceptPost(HttpServletRequest request) {
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
        String title = jsonNode.get("title").asText();
        String contentText = jsonNode.get("contentText").asText();
        //获取请求信息
        String token = request.getHeader("token");
        Claims claims;
        try {
            claims = JwtUtils.parseJwtToken(token);
        } catch (ExpiredJwtException e){
            return new ResponseResult<>(ResponseResult.TokenOutdated,"token已过期",null);
        }
        int userId = Integer.parseInt(claims.getId());
        //从ImagesTemp中获取user_id对应的Images
        try {
            List<PostContentImagesTemp> contentImagesTempList = postContentImagesTempDao.selectByUserId(userId);
            //先获取content_id
            PostContent postContent = new PostContent();
            postContent.setContentText(contentText);
            //保存Post_Content
            postContentDao.insert(postContent);
            int contentId = postContent.getId();
            System.out.println("contentId: "+contentId);
            //将temp中的数据转移到保存中
            Vector<Integer> deletedIds = new Vector<>();
            PostContentImages contentImage = new PostContentImages();
            for(PostContentImagesTemp temp : contentImagesTempList){
                contentImage.setUserId(userId);
                contentImage.setImageOrder(temp.getImageOrder());
                contentImage.setContentId(contentId);
                contentImage.setUrl(temp.getUrl());
                deletedIds.add(temp.getId());
                postContentImagesDao.insert(contentImage);
            }
            //删除temp中的数据
            postContentImagesTempDao.deleteBatchIds(deletedIds);
            //插入Post中的数据
            Post post = new Post();
            post.setUserId(userId);// Id
            post.setTitle(title);// Title
            post.setContentId(contentId);// ContentId
            Date currentDate = new Date();
            Timestamp timestamp = new Timestamp(currentDate.getTime());
            post.setDate(timestamp);// TimeStamp
            postDao.insert(post);
            return new ResponseResult<>(ResponseResult.AccessOk,"帖子上传成功",post);

        }catch (Exception e ){
            e.printStackTrace();
            return new ResponseResult<>(ResponseResult.Error, "服务器?出错", null);
        }


    }

    @Override
    public ResponseResult<Object> getPostByUserId(int userId){
        try{
            List<Post> postList = postDao.getPostByUserId(userId);
            List<ReplyPost> replyPosts = new ArrayList<>();
            PostContent postContent = new PostContent();
            List<PostContentImages> contentImages = new ArrayList<>();
            User user = userDao.getById(userId);
            //对要返回的数据重新构造
            System.out.println(postList);
            for(Post post: postList){
                ReplyPost replyPost = new ReplyPost();
                System.out.println(post);
                postContent = postContentDao.selectById(post.getContentId());
                contentImages = postContentImagesDao.selectByContentId(post.getContentId());
                replyPost.setPostId(post.getId()); // PostId
                replyPost.setTitle(post.getTitle()); // title
                replyPost.setContentText(postContent.getContentText()); //contentText
                replyPost.setUserId(user.getId());//userId
                replyPost.setUserNickname(user.getNickname());// userNickname
                replyPost.setUserHeadPortraitUrl(userHeadPortraitDao
                        .selectByUser_idUserHeadPortraitList(user.getId()).get(0).getUrl());//用户头像
                replyPost.setLikes(postLikesDao.selectCountByPostId(post.getId()));// likes 点赞数
                replyPost.setDate(post.getDate());// date
                List<String> imagesUrls = contentImages.stream()
                        .map(PostContentImages::getUrl)
                        .toList();
                replyPost.setImagesUrls(imagesUrls);// imagesUrls
                replyPosts.add(replyPost);
            }
            return new ResponseResult<>(ResponseResult.AccessOk,"获得帖子成功",replyPosts);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult<>(ResponseResult.Error,"服务器?出错",null);
        }


    }

    @Override
    public ResponseResult<Object> deleteUploadedImage(int imageOrder, int userId){
        try {
            //删掉imageOrder的记录
            postContentImagesTempDao.deleteByUserIdAndImageOrder(userId,imageOrder);
            //大于imageOrder的记录的imageOrder-1
            List<PostContentImagesTemp> contentImagesTempList = postContentImagesTempDao.selectByUserId(userId);
            for(PostContentImagesTemp contentImagesTemp : contentImagesTempList){
                int imageOrderTemp = contentImagesTemp.getImageOrder();
                if(imageOrderTemp>imageOrder){
                    contentImagesTemp.setImageOrder(imageOrderTemp-1);
                    postContentImagesTempDao.updateById(contentImagesTemp);
                }
            }

        }catch (Exception e){
            return  new ResponseResult<>(ResponseResult.Error,"服务器出错",null);
        }
        return new ResponseResult<>(ResponseResult.AccessOk,"删除成功",null);
    }

    @Override
    public ResponseResult<Object> getUploadedContentImages(String token){
        //获取请求信息
        Claims claims;
        try {
            claims = JwtUtils.parseJwtToken(token);
        } catch (ExpiredJwtException e){
            return new ResponseResult<>(ResponseResult.TokenOutdated,"token已过期",null);
        }
        int userId = Integer.parseInt(claims.getId());
        try {
            List<PostContentImagesTemp> contentImagesTempList = postContentImagesTempDao.selectByUserId(userId);
            return new ResponseResult<>(ResponseResult.AccessOk,"查询已帖子上传的图片成功",contentImagesTempList);
        }catch (Exception e){
            return new ResponseResult<>(ResponseResult.Error,"服务器出错",null);
        }


    }

    @Override
    public ResponseResult<Object> postLike(HttpServletRequest request, String token){
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
        int postId = Integer.parseInt(jsonNode.get("postId").asText());
        //解析token
        Claims claims;
        try {
            claims = JwtUtils.parseJwtToken(token);
        } catch (ExpiredJwtException e){
            return new ResponseResult<>(ResponseResult.TokenOutdated,"token已过期",null);
        }
        int userId = Integer.parseInt(claims.getId());
        List<PostLikes> postLikesList = postLikesDao.selectPostLikesByUserIdAndPostId(userId,postId);
        PostLikes postLikes = new PostLikes();
        if (postLikesList.isEmpty()){
            postLikes.setUserId(userId);
            postLikes.setPostId(postId);
            postLikesDao.insert(postLikes);
            return new ResponseResult<>(ResponseResult.AccessOk,"点赞成功",null);
        }else{
            return new ResponseResult<>(ResponseResult.Error,"点赞失败,已经点赞过了",null);
        }

    }

    @Override
    public ResponseResult<Object> postNoLike(HttpServletRequest request, String token){
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
        int postId = Integer.parseInt(jsonNode.get("postId").asText());
        //解析token
        Claims claims;
        try {
            claims = JwtUtils.parseJwtToken(token);
        } catch (ExpiredJwtException e){
            return new ResponseResult<>(ResponseResult.TokenOutdated,"token已过期",null);
        }
        int userId = Integer.parseInt(claims.getId());
        List<PostLikes> postLikesList = postLikesDao.selectPostLikesByUserIdAndPostId(userId,postId);
        PostLikes postLikes = new PostLikes();
        if (postLikesList.isEmpty()){
            return new ResponseResult<>(ResponseResult.Error,"取消点赞失败,未点赞",null);
        }else{
            postLikes = postLikesList.get(0);
            postLikesDao.deleteById(postLikes);
            return new ResponseResult<>(ResponseResult.AccessOk,"取消点赞成功",null);
        }
    }

    @Override
    public ResponseResult<Object> remark(HttpServletRequest request, String token){
        //获取请求中的json数据
        JsonNode jsonNode = JsonUtils.parseRequest(request);
        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
        int postId = Integer.parseInt(jsonNode.get("postId").asText());
        String contentText = jsonNode.get("contentText").asText();
        //解析token
        Claims claims;
        try {
            claims = JwtUtils.parseJwtToken(token);
        } catch (ExpiredJwtException e){
            return new ResponseResult<>(ResponseResult.TokenOutdated,"token已过期",null);
        }
        int userId = Integer.parseInt(claims.getId());
        PostRemark postRemark = new PostRemark();
        try {
            postRemark.setPostId(postId);//post_id
            postRemark.setContentText(contentText);//content_text
            postRemark.setUserId(userId);//user_id
            Date currentDate = new Date();
            Timestamp timestamp = new Timestamp(currentDate.getTime());
            postRemark.setDate(timestamp);//date
            postRemarkDao.insert(postRemark);
            return new ResponseResult<>(ResponseResult.AccessOk,"评论成功",postRemark);
        }catch (Exception e ){
            e.printStackTrace();
            return new ResponseResult<>(ResponseResult.Error, "服务器?出错", null);
        }

    }

//    public ResponseResult<Object> getPostRemark(HttpServletRequest request){
//        //获取请求中的json数据
//        JsonNode jsonNode = JsonUtils.parseRequest(request);
//        if (jsonNode == null) return new ResponseResult<>(ResponseResult.Error, "json格式出错", null);
//        int postId = Integer.parseInt(jsonNode.get("postId").asText());
//        //查询remarks
//        List<PostRemark> postRemarks = postRemarkDao.selectByPostId(postId);
//        //构造返回的ReplyRemarks
//        for(PostRemark postRemark: postRemarks){
//
//        }
//
//    }
}
