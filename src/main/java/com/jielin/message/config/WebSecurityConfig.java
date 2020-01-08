package com.jielin.message.config;

import com.jielin.message.security.CustomUserDetailsService;
import com.jielin.message.security.Sha1PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * security配置类
 *
 * @author yxl
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //静态资源所在的目录集合，不做拦截
    private static final String[] STATIC_RESOURCE =
            new String[]{"/js/**", "/css/**", "/images/**", "/layui/**"};
    //不需要拦截的动态资源
    private static final String[] HTTP_RESOURCE =
            new String[]{"/login", "/push/**", "/user/**", "/gt/**","/product/msg"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(STATIC_RESOURCE).permitAll()
                .antMatchers(HTTP_RESOURCE).permitAll()
                .anyRequest().authenticated().and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home")
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
        http.headers().frameOptions().sameOrigin();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    //通过sha1加密登录密码
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Sha1PasswordEncoder();
    }
}
