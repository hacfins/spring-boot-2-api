package com.langyastudio.edu.db.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.langyastudio.edu.db.model.UmsDivision;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@CacheConfig(cacheNames = "db" + ":umsdivisionmapper")
public interface UmsDivisionMapper extends Mapper<UmsDivision>
{
    /*-------------------------------------------------------------------------------------------------------------- */
    // cache
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     * 详情
     * @param pcdId
     * @return
     */
    @Cacheable(key = "#pcdId", unless = "#result == null")
    UmsDivision getByPcdId(@Param("pcdId")Integer pcdId);

    /*-------------------------------------------------------------------------------------------------------------- */
    // other
    /*-------------------------------------------------------------------------------------------------------------- */
    /**
     *
     * 省市区列表
     *
     * @param parentId
     * @param level
     * @param self
     * @param path
     * @param min
     * @param max
     * @return
     */
    List<String> getPcdIdBySchoolIdAndSgId(@Param("parentId")Integer parentId,
                                           @Param("level")Integer level, @Param("self") Integer self,
                                           @Param("path") String path, @Param("min")Integer min,
                                           @Param("max")Integer max);
}