package br.com.ecommerce.accounts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.ecommerce.accounts.dto.AddressDTO;
import br.com.ecommerce.accounts.dto.LoginDTO;
import br.com.ecommerce.accounts.dto.TokenDTO;
import br.com.ecommerce.accounts.dto.UserClientCreatedDTO;
import br.com.ecommerce.accounts.dto.UserClientDTO;
import br.com.ecommerce.accounts.dto.UserEmployeeCreatedDTO;
import br.com.ecommerce.accounts.dto.UserEmployeeDTO;
import br.com.ecommerce.accounts.exception.FailedCredentialsException;
import br.com.ecommerce.accounts.model.Address;
import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.UserBuilder;
import br.com.ecommerce.accounts.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	
	public UserClientCreatedDTO saveClientUser(UserClientDTO dto) {
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
				a.state())
		);

		repository.save(user);
		return new UserClientCreatedDTO(user);
	}
	public UserEmployeeCreatedDTO saveEmployeeUser(UserEmployeeDTO dto) {
		User user = new UserBuilder().createUserEmployee(
			dto.username(),
			dto.password(),
			dto.name()
		);
				
		repository.save(user);
		return new UserEmployeeCreatedDTO(user);
	}

	public TokenDTO auth(LoginDTO dto) {
		User user = repository.findByUsername(dto.username())
			.orElseThrow(EntityNotFoundException::new);

		if (!this.encoder.matches(dto.password(), user.getPassword()))
			throw new FailedCredentialsException("Bad credentials");
		
		return new TokenDTO(tokenService.generateToken(user));
	}
}