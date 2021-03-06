package com.langyastudio.edu.admin.bean.vo;

import com.langyastudio.edu.db.model.UmsApi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author langyastudio
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmsApiListVO implements Serializable
{
    List<UmsApi> roleApiInfos;
}
