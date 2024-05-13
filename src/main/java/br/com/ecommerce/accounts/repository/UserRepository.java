package br.com.ecommerce.accounts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecommerce.accounts.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);
}