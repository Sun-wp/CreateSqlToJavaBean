# 注意：

1.这里只尝试了由Navicat生成的创建sql（格式如下）

```sql
CREATE TABLE `comment` (
  `comment_id` varchar(255) NOT NULL COMMENT '评论id',
  `user_id` bigint(255) DEFAULT NULL COMMENT '用户id',
  `replay_id` bigint(255) DEFAULT NULL COMMENT '被评论用户id',
  `post_id` varchar(255) DEFAULT NULL COMMENT '帖子id',
  `parent_comment_id` varchar(255) DEFAULT NULL COMMENT '父级评论id',
  `create_comment_time` datetime DEFAULT NULL COMMENT '评论创建时间',
  `like_comment_num` int(255) DEFAULT '0' COMMENT '评论点赞人数',
  `unlike_comment_num` int(255) DEFAULT '0' COMMENT '评论点踩人数',
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

生成效果如下

```java
public class Comment{

	/**
	* 评论id
	*/
	private String commentId;

	/**
	* 用户id
	*/
	private Integer userId;

	/**
	* 被评论用户id
	*/
	private Integer replayId;

	/**
	* 帖子id
	*/
	private String postId;

	/**
	* 父级评论id
	*/
	private String parentCommentId;

	/**
	* 评论创建时间
	*/
	private Date createCommentTime;

	/**
	* 评论点赞人数
	*/
	private Integer likeCommentNum;

	/**
	* 评论点踩人数
	*/
	private Integer unlikeCommentNum;
}
```



2.除了行尾不要有多余的换行符\n，不然会报错的

3.这只是一个简陋的sql创建语句转javaBean的小工具类

4.转换的同时即可进行javaBean的输出，但……我不想改了^_^

5.还有进行大量处理的单线程操作应该用StringBuffer的，这里也不改了……毕竟一来操作也不多，二来简陋使用也占用不了太大内存

6.以及有兴趣的可以加上前端页面等进行更复杂的操作

7.很有可能有别人创建的更好的工具类（mybatis本身就有），it界有句话叫做“不要重复造轮子”，emmm但我写这个小工具类只是我的一时兴起，希望它能给你带来一定的帮助





# Note：

1.Here I only tried to create sql generated by Navicat (the format is as follows)

```sql
CREATE TABLE `comment` (
  `comment_id` varchar(255) NOT NULL COMMENT '评论id',
  `user_id` bigint(255) DEFAULT NULL COMMENT '用户id',
  `replay_id` bigint(255) DEFAULT NULL COMMENT '被评论用户id',
  `post_id` varchar(255) DEFAULT NULL COMMENT '帖子id',
  `parent_comment_id` varchar(255) DEFAULT NULL COMMENT '父级评论id',
  `create_comment_time` datetime DEFAULT NULL COMMENT '评论创建时间',
  `like_comment_num` int(255) DEFAULT '0' COMMENT '评论点赞人数',
  `unlike_comment_num` int(255) DEFAULT '0' COMMENT '评论点踩人数',
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

The effect is as follows

```java
public class Comment{

	/**
	* 评论id
	*/
	private String commentId;

	/**
	* 用户id
	*/
	private Integer userId;

	/**
	* 被评论用户id
	*/
	private Integer replayId;

	/**
	* 帖子id
	*/
	private String postId;

	/**
	* 父级评论id
	*/
	private String parentCommentId;

	/**
	* 评论创建时间
	*/
	private Date createCommentTime;

	/**
	* 评论点赞人数
	*/
	private Integer likeCommentNum;

	/**
	* 评论点踩人数
	*/
	private Integer unlikeCommentNum;
}
```

2. Don't have extra line breaks except the end of the line\n, otherwise you will get an error

3. This is just a simple gadget class for creating SQL statements to javaBean

4. The conversion of javaBean can be performed at the same time, but... I don’t want to change it ^_^

5. There is also a single-threaded operation that should use StringBuffer for a lot of processing, and it will not be changed here...After all, there are not many operations, and the second, the simple use does not take up too much memory.

6. And those who are interested can add front-end pages to perform more complex operations

7. It is very likely that there are better tool classes created by others (mybatis itself), there is a saying in the IT world called "Don't make wheels repeatedly", emmm but I wrote this small tool class just for a while Rise, I hope it can bring you some help