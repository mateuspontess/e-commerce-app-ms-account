package br.com.ecommerce.accounts.model;

public record UserClientCreatedDTO(
        Long id,
        String name,
        String email,
        String phone_number,
        String cpf,
        Address address) {
    public UserClientCreatedDTO(User user) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone_number(),
            user.getCpf(),
            user.getAddress()
        );
    }
}