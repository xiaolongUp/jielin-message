package com.jielin.message.security;

import com.jielin.message.config.ThirdApiConfig;
import com.jielin.message.dao.mysql.AdminRoleDao;
import com.jielin.message.dao.mysql.AdminUserDao;
import com.jielin.message.dto.ResponsePackDto;
import com.jielin.message.po.AdminRolePo;
import com.jielin.message.po.AdminUserPo;
import com.jielin.message.third.enums.ThirdActionEnum;
import com.jielin.message.util.HttpEntityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;

import java.net.URISyntaxException;
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

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ThirdApiConfig thirdApiConfig;

    //只有当用户权限为超级管理员的时候才可以登录此系统
    private static final String SUPER_ROLE = "super_role";

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        String url = thirdApiConfig.getJlWebApiUrl() + ThirdActionEnum.JL_WEB_ADMIN_USER.getActionName();
        String builder;
        try {
            builder = new URIBuilder(url).addParameter("userName", userName).build().toString();
        } catch (URISyntaxException e) {
            throw new UsernameNotFoundException("调用远程地址回去用户异常");
        }
        ResponseEntity<ResponsePackDto> result
                = restTemplate.exchange(builder, ThirdActionEnum.JL_WEB_ADMIN_USER.getRequestType(), null, ResponsePackDto.class);
        AdminUserPo user = null;
        if (result.getStatusCode().equals(HttpStatus.OK) &&
                result.getBody().getBody() != null) {
            user = (AdminUserPo) result.getBody().getBody();
        }

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

        String urlRole = thirdApiConfig.getJlWebApiUrl() + ThirdActionEnum.JL_WEB_ADMIN_ROLE.getActionName();
        String builderRole;
        try {
            builderRole = new URIBuilder(urlRole).addParameter("id", user.getRole().toString()).build().toString();
        } catch (URISyntaxException e) {
            throw new UsernameNotFoundException("调用远程地址回去用户权限异常");
        }
        ResponseEntity<ResponsePackDto> resultRole
                = restTemplate.exchange(builderRole, ThirdActionEnum.JL_WEB_ADMIN_ROLE.getRequestType(), null, ResponsePackDto.class);
        AdminRolePo userRole = null;
        if (resultRole.getStatusCode().equals(HttpStatus.OK) &&
                resultRole.getBody().getBody() != null) {
            userRole = (AdminRolePo) resultRole.getBody().getBody();
        }

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
