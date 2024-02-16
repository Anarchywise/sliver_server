# sliver_server
use for sliver_storm 

# 返回信息
## Code
1. WrongUserOrPassword = 301;
2. Unauthenticated = 302;
3. IllegalPhoneNum = 303;
4. IllegalPassword = 304;
5. WrongAuthCode = 305;
6. AuthCodeOutdated = 306;
7. IllegalAction = 307;
8. TokenOutdated = 308;
9. Error = 309;
10. IllegalNickname = 310;
11. WaitForAuthCode = 201;
12. LoginOk = 101;
13. RegisterOk = 102;
14. AccessOk = 103;

## 返回的结构
1. private int code;
2. private String message;
3. private T data;


用户名就是手机号,只能是手机号格式才能注册

# 接口
 1. 向后端发送的请求都是json格式的
 2. 比如 userId postId
 ~~~html
 {
   "userId": 1,
   "postId": 2
 }
 ~~~
# 用户相关接口

## 登录接口
1. /user/login 
2. Post
3. json请求格式
   1. username 用户名
   2. password 密码
4. 会返回一个token,访问其他接口(除了登录注册相关的)时需要把token放在请求的HEAD中,命名为token

## 测试接口
1. /test 
2. Get
3. 测试资源访问接口,需要携带token才会有返回值

## 尝试注册接口
1. /user/attemptRegister 
2. Post
3. json请求格式
   1. username 用户名
   2. password 密码
4. 发送请求后会要求等待验证码

## 验证码码注册接口
1. /user/CodeRegister 
2. Post
3. json请求格式:
   1. username 用户名
   2. password 密码
   3. authCode 验证码目前为111
4. 返回注册结果和token.

## 尝试注销接口
1. /user/attemptLogout 
2. Post
3. json请求格式:
   1. username 用户名
   2. password 密码
4. 发送请求后会要求等待验证码

## 验证码注销接口
1. /user/codeLogout 
2. Post

3. json请求格式:
   1. username 用户名
   2. password 密码
   3. authCode 验证码目前为222

4. 返回注销结果

## 用户反馈的接口
1. /user/feedback 
2. Post
3. json格式
   1. feedback 反馈的信息 目前只能是文本

## 用户修改密码接口
1. /user/changePassword 
2. Post
3. json格式
   1. username 用户名
   2. password 原来的密码
   3. changedPassword 改变后的密码

## 用户修改昵称的接口
1. /user/changeNickname 
2. Post
3. json格式
   1. username 用户名
   2. changedNickname 改变后的昵称

## 获取用户信息的接口
1. /user/details 
2. Post
3. json格式
   1. userId 需要查询的用户id 如果没有userId则返回本人的用户信息
4. 返回的格式
   1. 用户查询自己的和查询别人的是一样的,但是手机号会被隐藏
   ~~~html
   "data": {
        "id": 1, 用户id
        "username": "zh", 用户的手机号(用户名)
        "nickname": "zhanghao", 用户的昵称
        "password": "$2a$07$6NsBsT18Rus.yfjPhNEsN.bOH8JPR/pwDcklcUfIPlnG53hWNbar2", 用户的密文密码(不可反推,不用管)
        "email": "1981053231@qq.com", 用户的邮箱(没用)
        "phone": "18980435709", 用户的手机号(用户名)(写多了,难的改了)
        "authCode": null, 不用管
        "headPortraitUrl": "/user/headPortrait/$1$$20240209_080104$1706440868739.jpg" 用户头像的url
    }
    ~~~

## 用户上传头像的接口
1. /user/uploadHeadPortrait 
2. Post
3. 接收文件的代码:
   @PostMapping("/post/uploadImages")
   ResponseResult<Object> uploadPostImages(HttpServletRequest request, @RequestParam("file") MultipartFile file){
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
   int userId = Integer.parseInt(claims.getId());
   return postService.uploadPostContentImages(userId,file);
   }
4. 上传参照格式:
~~~html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Image Upload</title>
</head>
<body>
    <h2>Image Upload</h2>
    <form action="http://your-server-url/your-endpoint" method="post" enctype="multipart/form-data">
        <label for="image">Select Image:</label>
        <input type="file" name="file" id="image" accept="image/*">
        <br>
        <input type="submit" value="Upload">
    </form>
</body>
</html>
~~~

# 帖子相关接口

## 帖子的图片上传接口
1. /post/uploadedImages 
2. Post
3. 一次只能上传一个图片
4. 上传参照格式:
~~~html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Image Upload</title>
</head>
<body>
    <h2>Image Upload</h2>
    <form action="http://your-server-url/your-endpoint" method="post" enctype="multipart/form-data">
        <label for="image">Select Image:</label>
        <input type="file" name="file" id="image" accept="image/*">
        <br>
        <input type="submit" value="Upload">
    </form>
</body>
</html>
~~~

4. 接收文件的代码:
~~~html
   @PostMapping("/post/uploadImages")
   ResponseResult<Object> uploadPostImages(HttpServletRequest request, @RequestParam("file") MultipartFile file){
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
   int userId = Integer.parseInt(claims.getId());
   return postService.uploadPostContentImages(userId,file);
   }
~~~


## 上传帖子接口  {如果上传图片后没有上传帖子,会有问题,建议上传图片和帖子一次性上传}
1. /post/upload 
2. Post
3. json上传格式:
   1. title        帖子标题
   2. contentText  帖子内容{文本}
4. 返回上传结果


## 获取某个用户的帖子
1. /post/getUserPost 
2. Post
3. json上传格式:
   1. userId  需要查找的用户的id 没有userId则返回自己的帖子
4. 返回结果
   ~~~html
   "data": [
        {
            "title": "及你太美", 帖子的标题
            "userId": 1, 帖子发布者的id
            "likes": 0, 帖子的点赞数
            "userNickname": "zhanghao", 帖子发布者的昵称
            "userHeadPortraitUrl": "/user/headPortrait/$1$$20240209_080104$1706440868739.jpg", 帖子发布者的头像url
            "contentText": "hehhehehheeh", 帖子的呢容
            "date": "2024-02-08T19:07:21.000+00:00", 帖子发布的时间戳 (UTC)
            "imagesUrls": [], 帖子的图片的url
            "postId": 3 帖子的id
        },
        {
            "title": "及你太美",
            "userId": 1,
            "likes": 0,
            "userNickname": "zhanghao",
            "userHeadPortraitUrl": "/user/headPortrait/$1$$20240209_080104$1706440868739.jpg",
            "contentText": "hehhehehheeh",
            "date": "2024-02-08T19:08:02.000+00:00",
            "imagesUrls": [],
            "postId": 4
        },
    ]
    ~~~

## 对帖子进行评论接口
1. /post/uploadRemark  
2. Post
3. json上传格式:
   1. postId 帖子的id
   2. contentText 评论的内容
4. 目前只支持文本评论,不加图片,懒

## 获取帖子的评论接口
1. /post/getRemark 
2. Post
3. json格式:
   1. postId 帖子的id
4. 返回的格式
   ~~~html
   "data": [
        {
            "id": 1, 评论的id,不用管
            "userNickname": "zhanghao", 评论的用户的昵称
            "userHeadPortraitUrl": "/user/headPortrait/$1$$20240209_080104$1706440868739.jpg", 评论的用户的头像地址
            "contentText": "cxk666", 用户评论的内容
            "date": "2024-02-16T14:19:25.000+00:00" 时间戳(UTC)
        },
        {
            "id": 2,
            "userNickname": "zhanghao",
            "userHeadPortraitUrl": "/user/headPortrait/$1$$20240209_080104$1706440868739.jpg",
            "contentText": "cxk66666666666",
            "date": "2024-02-16T14:23:14.000+00:00"
        },
        {
            "id": 3,
            "userNickname": "zhanghao",
            "userHeadPortraitUrl": "/user/headPortrait/$1$$20240209_080104$1706440868739.jpg",
            "contentText": "cxk66666666666",
            "date": "2024-02-16T14:23:23.000+00:00"
        }
    ]
    ~~~

## 对帖子进行点赞的接口
1. /post/like 
2. Post
3. json格式
   1. postId 帖子的id

## 对帖子进行取消点赞的接口
1. /post/noLike 
2. Post
3. json格式
   1. postId 帖子的id
 
## 对帖子进行收藏的接口
1. /post/userCollect 
2. Post
3. json格式
   1. postId 帖子的id

## 查询用户的收藏的接口 
1. /post/userCollect 
2. Get
3. 返回的结果
   ~~~html
   "data": [
        {
            "id": 1,
            "userId": 1,
            "postId": 2 收藏的帖子的id
        },
        {
            "id": 2,
            "userId": 1,
            "postId": 3
        },
        {
            "id": 3,
            "userId": 1,
            "postId": 4
        }
    ]
    ~~~


# 旅游相关的接口
1. /travel/getRoute 
2. Post
3. json上传格式
   1. cityName 城市名 chengdu 和 gansu
   2. num  需要的景点个数
4. 返回的格式
   ~~~html
   "data": [
        {
            "id": 1, 旅游景点的顺序
            "positionName": "test7", 景点的名字
            "longitude": 7.0, 景点的经度
            "latitude": 7.0 景点的纬度s
        },
        {
            "id": 2,
            "positionName": "test1",
            "longitude": 1.0,
            "latitude": 1.0
        }
    ]
    ~~~

