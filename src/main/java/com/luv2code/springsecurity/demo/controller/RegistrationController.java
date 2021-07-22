package com.luv2code.springsecurity.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.luv2code.springsecurity.demo.user.CrmUser;
import com.luv2code.springsecurity.demo.entity.Role;
import com.luv2code.springsecurity.demo.entity.Users;
import com.luv2code.springsecurity.demo.service.UserService;

@Controller
@RequestMapping("/register")
public class RegistrationController {
	
    @Autowired
    private UserService userService;
    
    private List<Role> roles;
	
    private Logger logger = Logger.getLogger(getClass().getName());
    
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
	
	@PostConstruct
	protected void loadRoles() {
		// using hashmap, could also read this info from a database
		
		roles = new ArrayList<Role>();
		
		// key=the role, value=display to user
		roles.add(new Role("ROLE_EMPLOYEE"));
		roles.add(new Role("ROLE_MANAGER"));
		roles.add(new Role("ROLE_ADMIN"));
	}
	
	@GetMapping("/showRegistrationForm")
	public String showRegistrationForm(Model theModel) {
		
		theModel.addAttribute("crmUser", new CrmUser());
		
		// add roles to the model for form display
		theModel.addAttribute("roles", roles);
		
		return "registration-form";
	}

	@PostMapping("/processRegistrationForm")
	public String processRegistrationForm(
				@Valid @ModelAttribute("crmUser") CrmUser theCrmUser, 
				BindingResult theBindingResult, 
				Model theModel) {
		
		String userName = theCrmUser.getUserName();
		logger.info("Processing registration form for: " + userName);
		
		// form validation
		 if (theBindingResult.hasErrors()){
			 theModel.addAttribute("roles", roles);
			 return "registration-form";
			 
	        }
		 
		 List<String> formRoles = theCrmUser.getFormRoles();
		 if(!formRoles.contains("ROLE_EMPLOYEE")) {
			 formRoles.add("ROLE_EMPLOYEE");
		 }
		 
		 logger.info("-------> User details: " + theCrmUser);
		 
		 theCrmUser.setFormRoles(formRoles);

		// check the database if user already exists
        Users existing = userService.findByUserName(userName);
        if (existing != null){
        	theModel.addAttribute("crmUser", new CrmUser());
			theModel.addAttribute("registrationError", "User name already exists.");
			theModel.addAttribute("roles", roles);

			logger.warning("User name already exists.");
        	return "registration-form";
        }
        
        // create user account        						
        userService.save(theCrmUser);
        
        logger.info("Successfully created user: " + userName);
        
        return "registration-confirmation";		
	}
}
