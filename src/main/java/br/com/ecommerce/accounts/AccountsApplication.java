package br.com.ecommerce.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.ecommerce.accounts.configs.UserAdminCreation;

@SpringBootApplication
public class AccountsApplication implements CommandLineRunner {

	@Autowired
	UserAdminCreation uac;
	
	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		uac.createUserAdmin();
	}
}