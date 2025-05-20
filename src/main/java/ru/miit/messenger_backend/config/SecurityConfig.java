package ru.miit.messenger_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapClient;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.miit.messenger_backend.config.auth.JwtRequestFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Value("${spring.ldap.urls}")
    String ldapUrl;
    @Value("${spring.ldap.base}")
    String ldapBase;
    @Value("${spring.ldap.username}")
    String ldapUsername;
    @Value("${spring.ldap.password}")
    String ldapPassword;
    @Value("${ldap.user_filter}")
    String ldapUserFilter;
    @Value("${ldap.password_attribute}")
    String ldapPasswordAttribute;

    @Autowired
    private Environment env;

    @Lazy
    @Autowired
    private JwtRequestFilter jwtRequestFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var authorization = new Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>() {
            @Override
            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizationManagerRequestMatcherRegistry) {
                if (Arrays.asList(env.getActiveProfiles()).contains("dev")
                        || Arrays.asList(env.getActiveProfiles()).contains("rundev")) {
                    authorizationManagerRequestMatcherRegistry
                            .requestMatchers("/swagger-ui/**").permitAll()
                            .requestMatchers("/v3/api-docs/**").permitAll();
                }
                authorizationManagerRequestMatcherRegistry
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated();
            }
        };
        http
                .authorizeHttpRequests(authorization)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(List.of("*"));
                    configuration.setAllowedMethods(List.of("*"));
                    configuration.setAllowedHeaders(List.of("*"));
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", configuration);
                    httpSecurityCorsConfigurer.configurationSource(source);
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        jwtRequestFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userSearchFilter(ldapUserFilter)
                .contextSource()
                .url(ldapUrl + ldapBase)
                .managerDn(ldapUsername)
                .managerPassword(ldapPassword)
                .and()
                .passwordCompare()
                .passwordEncoder(new BCryptPasswordEncoder())
                .passwordAttribute(ldapPasswordAttribute);
    }

    @Bean
    public UserDetailsService getUserDetailsService(LdapContextSource ldapContextSource) {
        LdapUserDetailsService service = new LdapUserDetailsService(new FilterBasedLdapUserSearch("", ldapUserFilter, ldapContextSource));
        return username -> service.loadUserByUsername(username.trim());
    }

    @Bean
    public LdapClient ldapClient(LdapContextSource ldapContextSource) {
        return LdapClient.builder().contextSource(ldapContextSource).build();
    }

    @Bean
    AuthenticationManager myAuthenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider::authenticate;
    }
}
