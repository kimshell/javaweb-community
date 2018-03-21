package io.javaweb.community.service.general;


import io.javaweb.community.common.BaseMapper;
import io.javaweb.community.common.BaseOperation;
import io.javaweb.community.mybatis.domain.PageBounds;
import io.javaweb.community.mybatis.domain.PageList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * Created by KevinBlandy on 2017/10/30 13:44
 */
public abstract class GeneralService<T> implements BaseOperation<T>{

    @Autowired
    private BaseMapper<T> baseMapper;

    @Transactional(readOnly = true,rollbackFor = Exception.class)
    @Override
    public T queryByPrimaryKey(Serializable primaryKey) throws Exception {
        return this.baseMapper.queryByPrimaryKey(primaryKey);
    }

    @Transactional(readOnly = true,rollbackFor = Exception.class)
    @Override
    public PageList<T> queryByParamSelective(T entity, PageBounds pageBounds) throws Exception {
        return this.baseMapper.queryByParamSelective(entity,pageBounds);
    }

    @Transactional(readOnly = true,rollbackFor = Exception.class)
    @Override
    public T queryByParamSelectiveSole(T entity) throws Exception {
        return this.baseMapper.queryByParamSelectiveSole(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer create(T entity) throws Exception {
        return this.baseMapper.create(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateByPrimaryKeySelective(T entity) throws Exception {
        return this.baseMapper.updateByPrimaryKeySelective(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateByPrimaryKey(T entity) throws Exception {
        return this.baseMapper.updateByPrimaryKey(entity);
    }

    @Transactional
    @Override
    public Integer deleteByPrimaryKey(Serializable primaryKey) throws Exception {
        return this.baseMapper.deleteByPrimaryKey(primaryKey);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer deleteByParamSelective(T entity) throws Exception {
        return this.baseMapper.deleteByParamSelective(entity);
    }

    protected BaseMapper<T> getBaseMapper() {
        return baseMapper;
    }

//    public void setBaseMapper(BaseMapper<T> baseMapper) {
//        this.baseMapper = baseMapper;
//    }
}
