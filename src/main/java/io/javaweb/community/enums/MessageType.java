package io.javaweb.community.enums;

/**
 * Created by KevinBlandy on 2018/1/29 16:43
 */
public enum  MessageType {

    //新的帖子回复    用户id:用户名称   帖子id:页码:回复锚点 帖子标题     【OK】
    POST_REPLY("<a target=\"_blank\" href=\"http://www.javaweb.io/user/{0}\">{1}</a> 回复了 <a href=\"http://www.javaweb.io/post/{2}?page={3}#reply-{4}\">{5}</a>"),
    //新的回复回复    用户id:用户名称  帖子id:页码:回复锚点 帖子标题      【OK】
    REPLY_REPLY("<a target=\"_blank\" href=\"http://www.javaweb.io/user/{0}\">{1}</a> 在 <a href=\"http://www.javaweb.io/post/{2}?page={3}#reply-{4}\">{5}</a> 中回复了您。"),

    //新的主题提到    用户id:用户名称 帖子id:at锚点  帖子标题           【OK】
    POST_AT("<a target=\"_blank\" href=\"http://www.javaweb.io/user/{0}\">{1}</a> 在 <a href=\"http://www.javaweb.io/post/{2}#at-{3}\">{4}</a> 中提到了您。"),
    //新的回复提到    用户id:用户名称   帖子id:页码:at锚点 帖子标题       【OK】
    REPLY_AT("<a target=\"_blank\" href=\"http://www.javaweb.io/user/{0}\">{1}</a> 在 <a href=\"http://www.javaweb.io/post/{2}?page={3}#at-{4}\">{5}</a> 的回复中提到了您。"),

    //回复顶   用户id:用户名称   帖子id:页码:回复锚点 帖子标题
    REPLY_AGREE("<a target=\"_blank\" href=\"http://www.javaweb.io/user/{0}\">{1}</a> 在 <a href=\"http://www.javaweb.io/post/{2}?page={3}#reply-{4}\">{5}</a> 中对您的回复发表了态度。"),
    //回复踩   用户id:用户名称   帖子id:页码:回复锚点 帖子标题
    REPLY_DIS_AGREE("<a target=\"_blank\" href=\"http://www.javaweb.io/user/{0}\">{1}</a> 在 <a href=\"http://www.javaweb.io/post/{2}?page={3}#reply-{4}\">{5}</a> 中对您的回复发表了态度。"),

    //帖子顶   用户id:用户名称 帖子id:帖子名称
    POST_AGREE("<a target=\"_blank\" href=\"http://www.javaweb.io/user/{0}\">{1}</a> 对 <a href=\"http://www.javaweb.io/post/{2}\">{3}</a> 发表了态度。"),
    //帖子踩   用户id:用户名称 帖子id:帖子名称
    POST_DIS_AGREE("<a target=\"_blank\" href=\"http://www.javaweb.io/user/{0}\">{1}</a> 对 <a href=\"http://www.javaweb.io/post/{2}\">{3}</a> 发表了态度。");

	@Deprecated
    String content;

    MessageType(String content){
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
