package br.com.ecommerce.accounts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.ecommerce.accounts.exception.FailedCredentialsException;
import br.com.ecommerce.accounts.model.Address;
import br.com.ecommerce.accounts.model.AddressDTO;
import br.com.ecommerce.accounts.model.LoginDTO;
import br.com.ecommerce.accounts.model.TokenDTO;
import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.UserBuilder;
import br.com.ecommerce.accounts.model.UserClientCreatedDTO;
import br.com.ecommerce.accounts.model.UserClientDTO;
import br.com.ecommerce.accounts.model.UserEmployeeCreatedDTO;
import br.com.ecommerce.accounts.model.UserEmployeeDTO;
import br.com.ecommerce.accounts.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	public UserClientCreatedDTO createUserClient(UserClientDTO dto) {
		AddressDTO a = dto.address();
		User user = new UserBuilder().createUserClient(
				dto.username(),
				dto.password(),
				dto.name(),
				dto.email(),
				dto.phone_number(),
				dto.cpf(),
				new Address(
						a.street(),
						a.neighborhood(),
						a.postal_code(),
						a.number(),
						a.complement(),
						a.city(),
						a.state()))
				.build();

		repository.save(user);
		return new UserClientCreatedDTO(user);
	}
	public UserEmployeeCreatedDTO createUserEmployee(UserEmployeeDTO dto) {
		User user = new UserBuilder()
			.createUserEmployee(
				dto.username(),
				dto.password(),
				dto.name()
			)
			.build();
				
		repository.save(user);
		return new UserEmployeeCreatedDTO(user);
	}

	public TokenDTO auth(LoginDTO dto) {
		User user = repository.findByUsername(dto.username())
			.orElseThrow(EntityNotFoundException::new);

		if (!this.passwordEncoder.matches(dto.password(), user.getPassword()))
			throw new FailedCredentialsException("Bad credentials");
		
		return new TokenDTO(tokenService.generateToken(user));
	}
}