### 极光工作室三轮考核内容

# $\color{RED}{注意!严禁将整个项目copy作为工作室的个人考核内容,一旦发现,后果自负}$

项目结构

- com.day.expart 用于支付接口的同步提醒功能,需要部署到可以供外界访问的主机上
- demo 测试模块,无需理会
- 根目录 项目本体

需要修改的地方

- application.proerties 修改数据库相关配置

- com/day/examp3/utils/Cos.java 腾讯云COS的秘钥以及地址等配置

- com/day/examp3/controller/OrderController.java : 273-276行,345-347行 配置微信支付秘钥,同步通知地址,以及支付宝的秘钥等
- 
- com/day/examp3/utils/DynamicConfigUtil.java :64 需要配置项目的绝对路径

- com/day/examp3/utils/BackUpDataBaseManager.java : 36,38 配置sql文件的备份路径以及缓存目录

根目录有备份的sql文件,可自行使用
