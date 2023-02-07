package com.eikona.tech.config;


import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eikona.tech.constants.ApplicationConstants;

@Configuration
@EnableJpaAuditing(auditorAwareRef = ApplicationConstants.AUDITOR_PROVIDER)
public class JpaAuditingConfiguration implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (authentication == null)? Optional.of(ApplicationConstants.SYSTEM) :Optional.of(authentication.getName());
		
	}

    @Bean
    public AuditorAware<String> auditorProvider() {
		return () -> getCurrentAuditor();
    }
    
    /*
    if you are using spring security, you can get the currently logged username with following code segment.

    SecurityContextHolder.getContext().getAuthentication().getName()
    
   */
    
}
