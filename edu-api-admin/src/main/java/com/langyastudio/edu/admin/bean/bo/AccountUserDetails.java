package com.langyastudio.edu.admin.bean.bo;

import com.langyastudio.edu.db.model.UmsApi;
import com.langyastudio.edu.db.model.UmsUserAuth;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SpringSecurity需要的用户详情
 */
public class AccountUserDetails implements UserDetails
{
    private UmsUserAuth  umsAdmin;
    private List<UmsApi> resourceList;

    public AccountUserDetails(UmsUserAuth umsAdmin, List<UmsApi> resourceList)
    {
        this.umsAdmin = umsAdmin;
        this.resourceList = resourceList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        //返回当前用户的角色
        return resourceList.stream()
                .map(role -> new SimpleGrantedAuthority(role.getApiId() + ":" + role.getApiName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword()
    {
        return umsAdmin.getPwd();
    }

    @Override
    public String getUsername()
    {
        return umsAdmin.getUserName();
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return umsAdmin.getEnabled().equals(Byte.valueOf("1"));
    }
}
