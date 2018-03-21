package io.javaweb.community.entity.dto;

import io.javaweb.community.entity.CollectionEntity;
import io.javaweb.community.entity.PostEntity;

/**
 * Created by KevinBlandy on 2018/1/31 15:09
 */
public class CollectionDTO extends CollectionEntity{

    private static final long serialVersionUID = 2843073508070989032L;

    private PostEntity post;

    public PostEntity getPost() {
        return post;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }
}
