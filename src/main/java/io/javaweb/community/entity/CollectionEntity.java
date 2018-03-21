package io.javaweb.community.entity;

import io.javaweb.community.common.BaseEntity;
import io.javaweb.community.generate.Entity;
import io.javaweb.community.generate.Id;

/**
 * Created by KevinBlandy on 2018/1/29 12:20
 */
@Entity(table = "jw_collection",mapper = "io.javaweb.community.mapper.CollectionMapper")
public class CollectionEntity extends BaseEntity {

    private static final long serialVersionUID = 1499777655008872463L;

    @Id
    private String collectionId;

    private String postId;

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
