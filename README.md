# sliver_server
use for sliver_storm 


用户名就是手机号,只能是手机号格式才能注册

# 接口:



## 登录接口
1. /user/login :Post

2. json请求格式
username :
password :

3. 会返回一个token,访问其他资源接口时需要把token放在请求的HEAD中,命名为token

## 测试接口
1. /test :Get

2. 测试资源访问接口,需要携带token才会有返回值

## 尝试注册接口
1. /user/attemptRegister :Post

2. json请求格式
username:
password:

3. 发送请求后会要求等待验证码

## 验证码码注册接口
1. /user/CodeRegister :Post

2. json请求格式:
username:
password:
authCode:  验证码目前为111

3. 返回注册结果和token.

## 尝试注销接口
1. /user/attemptLogout :Post

2. json请求格式:
username:
password:

3. 发送请求后会要求等待验证码

## 验证码注销接口
1. /user/codeLogout :Post

2. json请求格式:
username:
password:
authCode:  验证码目前为222

3. 返回注销结果

# 帖子相关接口

## 帖子的图片上传接口
1. /post/uploadedImages :Post

2. 上传参照格式:
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

3. 返回上传结果

## 上传帖子接口
1. /post/upload :Post
2. json上传格式:
title        帖子标题
contentText  帖子内容
3. 返回上传结果


## 获取某个用户的帖子,没有userId则返回自己的帖子
