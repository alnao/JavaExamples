package it.alnao.gestUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
	private static String REALM = "REAME";
	
	private static final String[] USER_MATCHER = { "/utenti/cerca/**"};
	private static final String[] ADMIN_MATCHER = { "/utenti/inserisci/**", "/utenti/elimina/**" };

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
			http.csrf().disable()
				.authorizeRequests()
				.antMatchers(USER_MATCHER).hasAnyRole("USER")
				.antMatchers(ADMIN_MATCHER).hasAnyRole("ADMIN")
				.anyRequest().authenticated()
				.and()
				.httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint()).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	
	@Bean
	public AuthEntryPoint getBasicAuthEntryPoint()
	{
		return new AuthEntryPoint();
	}

	/* To allow Pre-flight [OPTIONS] request from browser */
	@Override
	public void configure(WebSecurity web) throws Exception
	{
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	};
	
	@Bean
	@Override
	public UserDetailsService userDetailsService()
	{
		UserBuilder users = User.builder();

		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

		manager.createUser(users
				.username("read")
				.password(new BCryptPasswordEncoder().encode("bim"))
				.roles("USER").build());

		manager.createUser(users
				.username("admin")
				.password(new BCryptPasswordEncoder().encode("bum"))
				.roles("USER", "ADMIN").build());

		return manager;
	}

}
