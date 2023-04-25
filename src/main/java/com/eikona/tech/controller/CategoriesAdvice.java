package com.eikona.tech.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.eikona.tech.entity.User;
import com.eikona.tech.repository.UserRepository;
import com.eikona.tech.service.OrganizationService;

@ControllerAdvice
public class CategoriesAdvice {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrganizationService organizationService;

	@ModelAttribute
	public void addAttributes(Model model, Principal principal) {

		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = "";
		if(null != user.getOrganization()) {
			orgName = organizationService.getById(user.getOrganization().getId()).getName();
		}
		
		model.addAttribute("OrgName", orgName);
	}
}
