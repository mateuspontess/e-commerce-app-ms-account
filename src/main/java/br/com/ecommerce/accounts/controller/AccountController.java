package br.com.ecommerce.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.accounts.model.LoginDTO;
import br.com.ecommerce.accounts.model.TokenDTO;
import br.com.ecommerce.accounts.model.UserClientCreatedDTO;
import br.com.ecommerce.accounts.model.UserClientDTO;
import br.com.ecommerce.accounts.model.UserEmployeeCreatedDTO;
import br.com.ecommerce.accounts.model.UserEmployeeDTO;
import br.com.ecommerce.accounts.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private UserService service;
	
	
	@PostMapping("/auth")
	public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO dto) {
		return ResponseEntity.ok(service.auth(dto));
	}
	
	@PostMapping("/create")
	@Transactional
	public ResponseEntity<UserClientCreatedDTO> createClientUser(@RequestBody @Valid UserClientDTO dto) {
		UserClientCreatedDTO userData = service.createUserClient(dto);
		return ResponseEntity.ok().body(userData);
	}
	
	@PostMapping("/create/employee")
	@Transactional
	public ResponseEntity<UserEmployeeCreatedDTO> createEmployeeUser(@RequestBody @Valid UserEmployeeDTO dto) {
		UserEmployeeCreatedDTO userData = service.createUserEmployee(dto);
		return ResponseEntity.ok().body(userData);
	}
}