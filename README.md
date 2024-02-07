# sliver_server
use for sliver_strom 


用户名就是手机号,只能是手机号格式才能注册

# 接口:

/user/login

## 登录接口

表单:
username :
password :

会返回一个token,访问其他资源接口时需要把token放在请求的HEAD中,命名为token

## 测试接口
/test

测试资源访问接口,需要携带token才会有返回值

## 尝试注册接口

/user/attemptRegister

表单:
username:
password:


发送请求后会要求等待验证码

## 验证码码注册接口

/user/CodeRegister

表单:
username:
password:
authCode:  验证码目前为111

返回注册结果和token.

## 尝试注销接口

/user/attemptLogout

表单:
username:
password:

发送请求后会要求等待验证码


## 验证码注销接口

/user/codeLogout

表单:
username:
password:
authCode:  验证码目前为222

返回注销结果

