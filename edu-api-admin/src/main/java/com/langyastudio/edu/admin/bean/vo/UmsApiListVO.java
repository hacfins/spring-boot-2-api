package com.langyastudio.edu.admin.bean.vo;

import com.langyastudio.edu.db.model.UmsApi;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author langyastudio
 */
@Data
@AllArgsConstructor
public class UmsApiListVO
{
    List<UmsApi> roleApiInfos;
}
