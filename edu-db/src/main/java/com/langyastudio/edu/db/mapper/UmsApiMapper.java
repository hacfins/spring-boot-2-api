package com.langyastudio.edu.db.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.langyastudio.edu.db.model.UmsApi;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

/**
 * API 权限表
 * 逻辑Id号 - api_id
 */
@CacheConfig(cacheNames = "db" + ":umsapimapper")
public interface UmsApiMapper extends Mapper<UmsApi>
{
    /*-------------------------------------------------------------------------------------------------------------- */
    // cache
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     * 根据 api_id 获取APi权限信息
     * @return
     */
    @Cacheable(key = "#apiId", unless = "#result == null")
    UmsApi getInfoByApiId(@Param("apiId")String apiId);

    /**
     * 获取所有的ApiIds
     * @return
     */
    List<String> getApiIdsAll();
}