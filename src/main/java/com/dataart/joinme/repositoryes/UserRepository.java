package com.dataart.joinme.repositoryes;

import com.dataart.joinme.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    User save(User user);
}
