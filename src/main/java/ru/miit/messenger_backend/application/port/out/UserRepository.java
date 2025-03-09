package ru.miit.messenger_backend.application.port.out;

import org.jmolecules.architecture.hexagonal.SecondaryPort;
import org.jmolecules.ddd.annotation.Repository;
import org.springframework.data.repository.CrudRepository;
import ru.miit.messenger_backend.application.domain.model.User;

import java.util.Optional;

@Repository
@org.springframework.stereotype.Repository
@SecondaryPort
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> getUserByUid(String uid);

    Optional<User> getUserById(int id);
}
