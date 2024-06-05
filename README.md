仅仅只需要在Dao层添加@DBRouter注解并添加对应的分库分表key值就可以实现分库分表
    @DBRouter(key = "userId")
    User queryUserInfoByUserId(User req);
