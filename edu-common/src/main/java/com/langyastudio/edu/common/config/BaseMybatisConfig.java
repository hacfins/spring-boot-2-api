package com.langyastudio.edu.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * Mybatis Plus
 *
 * 【需要被继承】
 *
 * @author langyastudio
 */
public class BaseMybatisConfig
{
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor()
    {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 【分页】
        // 新的分页插件,一缓和二缓遵循mybatis的规则
        // 需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        // 【安全】
        // 防止全表更新与删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        // 【乐观锁】
        // 当要更新一条记录的时候，希望这条记录没有被别人更新
        //  支持的数据类型只有:int,Integer,long,Long,Date,Timestamp,LocalDateTime
        //  整数类型下 newVersion = oldVersion + 1
        //  newVersion 会回写到 bean 中
        //  仅支持 updateById(id) 与 update(entity, wrapper) 方法
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}