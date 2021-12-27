package com.langyastudio.edu.portal.bean.vo;
import com.langyastudio.edu.db.model.UmsDivision;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UmsDivisionVO extends UmsDivision
{
    private List<UmsDivisionVO> children;

    private boolean hasParent = false;
    private boolean hasChild  = false;

    public void initChildren()
    {
        children = new ArrayList<>();
    }
}
