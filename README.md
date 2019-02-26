## 项目目录结构
``` lua
|-- spring-boot-2
    |-- java
    |   |-- com
    |       |-- springboot 
    |           |-- MybatisGenerator.java --mybatis自动生成映射        
    |           |-- Study2019Application.java --springboot项目启动入口
    |           |-- comm -- 公用包
    |           |   |-- aop -- spring aop 日志记录
    |           |   |-- base -- 基础类 BaseDao BaseController
    |           |   |-- config -- spring boot 配置和插件
    |           |   |-- Enum -- 公用参数/枚举
    |           |   |-- exception -- 自定义异常
    |           |   |-- filter -- 过滤器
    |           |   |-- page -- 分页
    |           |   |-- result -- 返回结果
    |           |   |-- utils -- 工具类
    |           |-- controller -- 控制层
    |           |-- dao -- mapper映射
    |           |-- domain -- 实体类
    |           |-- service -- 逻辑层
    |           |-- vo -- 自定义实体
    |-- resources -- 资源包
        |-- config -- springboot配置文件
        |-- generator -- mybatis-generator配置文件
        |-- logback -- 日志配置文件
        |-- mappers -- mapper映射文件
        |-- static -- 静态文件
        |-- templates -- html模板
```
