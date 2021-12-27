package com.langyastudio.edu.common.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfo<T>
{
    /**
     * 总条数
     */
    private Long    total;
    /**
     * 列表
     */
    private List<T> list;
}
