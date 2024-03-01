package com.zh.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.dao.*;
import com.zh.entity.*;
import com.zh.service.PostService;
import com.zh.utils.LegalUtils;
import com.zh.utils.PictureUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    UserPostCollectDao userPostCollectDao;

    @Autowired
    PostLikesDao postLikesDao;

    @Autowired
    PostRemarkDao postRemarkDao;

    @Autowired
    PostTypeDao postTypeDao;

    @Override
    public ResponseResult<Object> uploadPostContentImages(int userId, MultipartFile getFile) {

           // 获取文件名
            String originalFilename = getFile.getOriginalFilename();
            // 判断文件是否合法
            ResponseResult<Object> object = LegalUtils.isImageFileName(originalFilename);
            if (!Objects.isNull(object)) return object;

            // 压缩图片
            byte[] compressedImage;
            try {
                compressedImage = PictureUtils.compressImage(getFile.getBytes());
            } catch (IOException e) {
                System.out.println("压缩图片失败");
                return new ResponseResult<>(ResponseResult.Error, "压缩图片失败", null);
            }

            // 构建文件路径
            String userDir = System.getProperty("user.dir"); // 获取当前工作目录
            String accessPath = "http://39.101.67.214:8080/post/images/" + LegalUtils.buildAccessPath(userId, originalFilename);
            String relativePath = "data/post/images/" + LegalUtils.buildAccessPath(userId, originalFilename);
            String absolutePath = Paths.get(userDir, relativePath).toString(); // 将当前工作目录与相对路径结合，创建绝对路径
            System.out.println("postUploadFile Absolute Path: " + absolutePath);
            System.out.println("PostImageAccessPath: " + accessPath);

            // 保存文件到服务器
            try {
                Files.createDirectories(Paths.get(absolutePath).getParent());
                Files.write(Paths.get(absolutePath), compressedImage);
                System.out.println("File saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save file.");
                return new ResponseResult<>(ResponseResult.Error, "保存文件失败", null);
            }

            //构建临时上传图像
            PostContentImagesTemp contentImagesTempSaved = new PostContentImagesTemp();
            contentImagesTempSaved.setUrl(accessPath);
            contentImagesTempSaved.setUserId(userId);
            //保存到数据库
            List<PostContentImagesTemp> contentImagesTemp1 = postContentImagesTempDao.selectByUserIdOrderByImage_orderDescInt(userId);
            if(contentImagesTemp1.isEmpty()){
                contentImagesTempSaved.setImageOrder(1);
            }else{
                System.out.println(contentImagesTemp1);
                contentImagesTempSaved.setImageOrder(contentImagesTemp1.get(0).getImageOrder()+1);
            }
            postContentImagesTempDao.insert(contentImagesTempSaved);
            System.out.println(contentImagesTempSaved);
            return new ResponseResult<>(ResponseResult.AccessOk,"上传成功",null);

    }

    @Override
    public ResponseResult<Object> acceptPost(int userId, String title, String contentText,String type) {
        //获取临时保存图片
        List<PostContentImagesTemp> contentImagesTempList = postContentImagesTempDao.selectByUserId(userId);//从ImagesTemp中获取user_id对应的Images
        PostContent postContent = new PostContent();//先获取content_id
        postContent.setContentText(contentText);
        postContentDao.insert(postContent);//保存Post_Content
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
            contentImage.setId(null);
        }

        //删除temp中的数据
        if(!deletedIds.isEmpty()) postContentImagesTempDao.deleteBatchIds(deletedIds);

        //插入Post中的数据
        Post post = new Post();
        post.setUserId(userId);// Id
        post.setTitle(title);// Title
        post.setContentId(contentId);// ContentId
        Date currentDate = new Date();
        Timestamp timestamp = new Timestamp(currentDate.getTime());
        post.setDate(timestamp);// TimeStamp
        postDao.insert(post);

        //插入postType中的数据
        PostType postType = new PostType();
        postType.setPostId(post.getId());
        postType.setType(type);
        postTypeDao.insert(postType);
        return new ResponseResult<>(ResponseResult.AccessOk,"帖子上传成功",post);
    }

    @Override
    public ResponseResult<Object> getPostByUserId(int userId){
        List<Post> postList = postDao.getPostByUserId(userId);
        List<ReplyPost> replyPosts = new ArrayList<>();
        PostContent postContent = new PostContent();
        List<PostContentImages> contentImages = new ArrayList<>();
        User user = userDao.getById(userId);
        //对要返回的数据重新构造
        System.out.println(postList);
        for(Post post: postList){
            ReplyPost replyPost = new ReplyPost();
            postContent = postContentDao.selectById(post.getContentId());
            contentImages = postContentImagesDao.selectByContentId(post.getContentId());
            replyPost.setPostId(post.getId()); // PostId
            replyPost.setTitle(post.getTitle()); // title
            replyPost.setContentText(postContent.getContentText()); //contentText
            replyPost.setUserId(user.getId());//userId
            replyPost.setUserNickname(user.getNickname());// userNickname
            List<UserHeadPortrait> userHeadPortraits = userHeadPortraitDao
            .selectByUser_idUserHeadPortraitList(user.getId());
            if(!userHeadPortraits.isEmpty()) replyPost.setUserHeadPortraitUrl(userHeadPortraits.get(0).getUrl());//用户头像
            replyPost.setLikes(postLikesDao.selectCountByPostId(post.getId()));// likes 点赞数
            replyPost.setDate(post.getDate());// date
            List<String> imagesUrls = contentImages.stream()
                    .map(PostContentImages::getUrl)
                    .toList();
            replyPost.setImagesUrls(imagesUrls);// imagesUrls
            replyPosts.add(replyPost);
        }
        return new ResponseResult<>(ResponseResult.AccessOk,"获得帖子成功",replyPosts);
    }

    @Override
    public ResponseResult<Object> deleteUploadedImage(int imageOrder, int userId){
    
        //删掉imageOrder的记录
        if(!postContentImagesTempDao.deleteByUserIdAndImageOrder(userId,imageOrder)) return new ResponseResult<>(ResponseResult.Error,"删除失败",null);;
        //大于imageOrder的记录的imageOrder-1
        List<PostContentImagesTemp> contentImagesTempList = postContentImagesTempDao.selectByUserId(userId);
        for(PostContentImagesTemp contentImagesTemp : contentImagesTempList){
            int imageOrderTemp = contentImagesTemp.getImageOrder();
            if(imageOrderTemp>imageOrder){
                contentImagesTemp.setImageOrder(imageOrderTemp-1);
                postContentImagesTempDao.updateById(contentImagesTemp);
            }
        }
        
        return new ResponseResult<>(ResponseResult.AccessOk,"删除成功",null);
    }

    @Override
    public ResponseResult<Object> getUploadedContentImages(int userId){
        List<PostContentImagesTemp> contentImagesTempList = postContentImagesTempDao.selectByUserId(userId);
        return new ResponseResult<>(ResponseResult.AccessOk,"查询已帖子上传的图片成功",contentImagesTempList);
    }

    @Override
    public ResponseResult<Object> postLike(int userId, int postId){
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
    public ResponseResult<Object> postNoLike(int userId, int postId){

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
    public ResponseResult<Object> remark(int postId, String contentText, int userId){

        PostRemark postRemark = new PostRemark();
        postRemark.setPostId(postId);//post_id
        postRemark.setContentText(contentText);//content_text
        postRemark.setUserId(userId);//user_id
        Date currentDate = new Date();
        Timestamp timestamp = new Timestamp(currentDate.getTime());
        postRemark.setDate(timestamp);//date
        postRemarkDao.insert(postRemark);
        return new ResponseResult<>(ResponseResult.AccessOk,"评论成功",postRemark);

    }
    @Override
    public ResponseResult<Object> getPostRemark(int postId){
        //查询remarks
        List<PostRemark> postRemarks = postRemarkDao.selectByPostId(postId);
        List<ReplyRemark> replyRemarks = new ArrayList<>();
        //构造返回的ReplyRemarks
        for(PostRemark postRemark: postRemarks){
            ReplyRemark replyRemark = new ReplyRemark();
            replyRemark.setId(postRemark.getId());// id
            User user = userDao.getById(postRemark.getUserId());
            List<UserHeadPortrait> userHeadPortraits = userHeadPortraitDao.selectByUser_idUserHeadPortraitList(user.getId());
            replyRemark.setUserId(user.getId());//userId
            replyRemark.setUserNickname(user.getNickname());//nickname
            if(!userHeadPortraits.isEmpty()){
                replyRemark.setUserHeadPortraitUrl(userHeadPortraits.get(0).getUrl());//headportraiturl
            }
            
            replyRemark.setContentText(postRemark.getContentText());//contentText
            replyRemark.setDate(postRemark.getDate());//date
            replyRemarks.add(replyRemark);
        }
        return new ResponseResult<>(ResponseResult.AccessOk,"查询评论成功",replyRemarks);
       
    }
    @Override
    public ResponseResult<Object> postCollect(int postId, int userId){
        UserPostCollect userPostCollect =new UserPostCollect();
        userPostCollect.setUserId(userId);//userId
        userPostCollect.setPostId(postId);// postId
        userPostCollectDao.insert(userPostCollect);
        return new ResponseResult<>(ResponseResult.AccessOk,"收藏成功",userPostCollect);
    }
    @Override
    public ResponseResult<Object> getUserPostCollects(int userId){
        List<UserPostCollect> userPostCollects = userPostCollectDao.selectByUserId(userId);
        return new ResponseResult<>(ResponseResult.AccessOk,"查询用户收藏成功",userPostCollects);
    }

    @Override
    public ResponseResult<Object> getPostByPage(int current,int size ) {
        // TODO Auto-generated method stub
        // 构建查询条件
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("date");//按时间降序排序
        // 创建分页对象 
        Page<Post> page = new Page<>(current+1, size,true);
        // 执行查询
        IPage<Post> postPage = postDao.selectPage(page, queryWrapper);
        List<Post> postList = postPage.getRecords();
        List<ReplyPost> replyPosts = new ArrayList<>();
        PostContent postContent = new PostContent();
        List<PostContentImages> contentImages = new ArrayList<>();
        //对要返回的数据重新构造
        // 输出查询条件
        System.out.println("Query Wrapper: " + queryWrapper.toString());
        // 输出分页信息
        System.out.println("Total: " + postPage.getTotal());
        System.out.println("Current Page: " + postPage.getCurrent());
        System.out.println("Page Size: " + postPage.getSize());
        for(Post post: postList){
            User user = userDao.getById(post.getUserId());
            ReplyPost replyPost = new ReplyPost();
            postContent = postContentDao.selectById(post.getContentId());
            contentImages = postContentImagesDao.selectByContentId(post.getContentId());
            replyPost.setPostId(post.getId()); // PostId
            replyPost.setTitle(post.getTitle()); // title
            replyPost.setContentText(postContent.getContentText()); //contentText
            replyPost.setUserId(user.getId());//userId
            replyPost.setUserNickname(user.getNickname());// userNickname
            List<UserHeadPortrait> headList = userHeadPortraitDao.selectByUser_idUserHeadPortraitList(user.getId());
            if(!headList.isEmpty()){
                replyPost.setUserHeadPortraitUrl(headList.get(0).getUrl());//用户头像url
            }
            replyPost.setLikes(postLikesDao.selectCountByPostId(post.getId()));// likes 点赞数
            replyPost.setDate(post.getDate());// date
            List<String> imagesUrls = contentImages.stream()
                    .map(PostContentImages::getUrl)
                    .toList();
            replyPost.setImagesUrls(imagesUrls);// postimagesUrls
            Map<String,Object> postIdMap = new HashMap<>();
            postIdMap.put("post_id",post.getId());
            replyPost.setType(postTypeDao.selectByMap(postIdMap).get(0).getType());//postType
            replyPosts.add(replyPost);
        }
        return new ResponseResult<>(ResponseResult.AccessOk,"获得帖子成功",replyPosts);
    }
}
