package com.eikona.tech.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Organization;
import com.eikona.tech.entity.Role;
import com.eikona.tech.entity.User;
import com.eikona.tech.repository.OrganizationRepository;
import com.eikona.tech.repository.UserRepository;
import com.eikona.tech.service.PrivilegeService;
import com.eikona.tech.service.RoleService;

@Controller
public class RoleController {
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private PrivilegeService privilegeService;
	
	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/role")
	@PreAuthorize("hasAuthority('role_view')")
	public String list() {
		return "role/role_list";
	}
	
	@GetMapping("/role/new") 
	@PreAuthorize("hasAuthority('role_create')")
	public String newRole(Model model, Principal principal) {
		
		Role role = new Role(); 
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> orgList = null;
		if(null == user.getOrganization()) {
			orgList = (List<Organization>) organizationRepository.findAll();
		}else {
			orgList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		model.addAttribute("listOrganization", orgList);
		model.addAttribute("listPrivilege", privilegeService.getAll());
		model.addAttribute("role", role); 
		model.addAttribute("title","New Role");
		return "role/role_new"; 
	}

	@PostMapping("/role/add")
	@PreAuthorize("hasAnyAuthority('role_create','role_update')")
	public String saveRole(@ModelAttribute("role") Role role,@Valid Role orgz, Errors errors,String title,
			Model model, Principal principal) {

		if (errors.hasErrors()) {
			model.addAttribute("title",title);
			User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
			List<Organization> orgList = null;
			if(null == user.getOrganization()) {
				orgList = (List<Organization>) organizationRepository.findAll();
			}else {
				orgList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
			}
			model.addAttribute("listOrganization", orgList);
			model.addAttribute("listPrivilege", privilegeService.getAll());
    		return "/role/role_new";
    	}else {
 			if(null==role.getId())
 			  roleService.save(role);
 			else {
 				Role roleObj = roleService.getById(role.getId());
 				role.setCreatedBy(roleObj.getCreatedBy());
 				role.setCreatedDate(roleObj.getCreatedDate());
 	 			roleService.save(role);
 			}
 		 	return "redirect:/role";
    	 }	
    }

	@GetMapping("/role/edit/{id}")
	@PreAuthorize("hasAuthority('role_update')")
	public String editRole(@PathVariable(value = "id") long id, Model model, Principal principal) {
		Role role = roleService.getById(id);
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> orgList = null;
		if(null == user.getOrganization()) {
			orgList = (List<Organization>) organizationRepository.findAll();
		}else {
			orgList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		model.addAttribute("listOrganization", orgList);
		model.addAttribute("listPrivilege", privilegeService.getAll());
		model.addAttribute("role", role);
		model.addAttribute("title","Update Role");
		return "role/role_new";
	}

	@GetMapping("/role/delete/{id}")
	@PreAuthorize("hasAuthority('role_delete')")
	public String deleteRole(@PathVariable(value = "id") long id) {
		this.roleService.deleteById(id);
		return "redirect:/role";
	}
	
	@RequestMapping(value = "/api/search/role", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('role_view')")
	public @ResponseBody PaginationDto<Role> searchRole(Long id, String name, int pageno, String sortField, String sortDir, Principal principal) {
		
		User userObj = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == userObj.getOrganization()?null:userObj.getOrganization().getName());
		
		PaginationDto<Role> dtoList = roleService.searchByField(id, name,  pageno, sortField, sortDir, orgName);
		return dtoList;
	}
}
