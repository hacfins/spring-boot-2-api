package com.langyastudio.edu.admin.bean.vo;
import com.langyastudio.edu.db.model.UmsDivision;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UmsDivisionVO extends UmsDivision implements Serializable
{
    private List<UmsDivisionVO> children;

    private boolean hasParent = false;
    private boolean hasChild  = false;

    public void initChildren()
    {
        children = new ArrayList<>();
    }
}
