package io.javaweb.community.mapper;

import io.javaweb.community.common.BaseMapper;
import io.javaweb.community.entity.CollectionEntity;
import io.javaweb.community.entity.dto.CollectionDTO;
import io.javaweb.community.mybatis.domain.PageBounds;
import io.javaweb.community.mybatis.domain.PageList;
import org.springframework.stereotype.Repository;

/**
 * Created by KevinBlandy on 2018/1/29 12:22
 */
@Repository
public interface CollectionMapper extends BaseMapper<CollectionEntity>{

    //检索
    PageList<CollectionDTO> queryCollectionsByCreateUser(CollectionDTO collectionDTO, PageBounds pageBounds)throws Exception;
}
