package com.jielin.message.security;

import com.jielin.message.dao.mysql.AdminRoleDao;
import com.jielin.message.dao.mysql.AdminUserDao;
import com.jielin.message.po.AdminRolePo;
import com.jielin.message.po.AdminUserPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 自定义UserDetailsService
 *
 * @author yxl
 */
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminUserDao adminUserDao;

    @Autowired
    private AdminRoleDao adminRoleDao;

    //只有当用户权限为超级管理员的时候才可以登录此系统
    private static final String SUPER_ROLE = "super_role";

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        AdminUserPo user = adminUserDao.findUserByUsername(userName);

        if (null == user) {
            log.warn("User.notFound", new Object[]{userName},
                    "Username {0} not found");
            throw new UsernameNotFoundException("账户不存在");
        }
        if (!user.getEnabled()) {
            log.warn("User.frozen", new Object[]{userName},
                    "Username {0} not enabled");
            throw new UsernameNotFoundException("账户被冻结");
        }
        if (user.getStatus().intValue() != 1) {
            log.warn("User.abnormal", new Object[]{userName},
                    "Username {0} status not normal");
            throw new UsernameNotFoundException("账户状态异常");
        }
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        AdminRolePo userRole = adminRoleDao.selectByPrimaryKey(user.getRole());

        String role = "DEFAULT";
        if (userRole != null) {
            role = userRole.getDescription();
        }
        if (!SUPER_ROLE.equals(role)) {
            log.warn("User.notAllowed", new Object[]{userName},
                    "Username {0} status not normal");
            throw new AuthenticationCredentialsNotFoundException("账户权限不足");
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        authorities.add(authority);

        return new User(user.getEmail(), user.getPassword(), authorities);
    }
}
