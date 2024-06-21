package br.com.ecommerce.accounts.dto;

import br.com.ecommerce.accounts.model.User;

public record UserEmployeeCreatedDTO(
        Long id,
        String username,
        String name) {
            
    public UserEmployeeCreatedDTO(User user) {
        this(
            user.getId(),
            user.getUsername(),
            user.getName()
        );
    }
}