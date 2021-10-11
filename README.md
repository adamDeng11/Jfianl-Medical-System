基于Jfinal架构医疗预约挂号系统

后台技术:
jfinal生态少，有问题只能看官方文档，慎用
Jfinal+Boostrap+Maven+MSQL

说明：
服务器部署直接利用jfinal-undertow 部署
添加以下文件package.xml、jfinal.sh(linux)、jfinal.bat(windows)并修改相关配置
详细部署链接：https://jfinal.com/doc/1-3

mysql配置

# config
jdbcUrl = jdbc:mysql://localhost:3306/medical_system?characterEncoding=utf8&useSSL=false&zeroDateTimeBehavior=convertToNull
user = "自己账号"
password = "自己密码"
devMode = true

uploadPath=C://medicalSystemFiles

#每个时段一个医生限制预约多少人
timeLimit = 50


