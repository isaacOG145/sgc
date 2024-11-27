package utez.edu._b.sgc.users.control;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu._b.sgc.customer.model.Customer;
import utez.edu._b.sgc.role.model.Role;
import utez.edu._b.sgc.role.model.RoleRepository;
import utez.edu._b.sgc.users.model.User;
import utez.edu._b.sgc.users.model.UserDto;
import utez.edu._b.sgc.users.model.UserRepository;
import utez.edu._b.sgc.utils.Message;
import utez.edu._b.sgc.utils.TypesResponse;

import java.sql.SQLException;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private void validateUserData(UserDto dto) {
        if (dto.getName().length() > 50) {
            throw new IllegalArgumentException("El nombre excede 50 caracteres");
        }
        if (dto.getLastName().length() > 100) {
            throw new IllegalArgumentException("El apellido excede 100 caracteres");
        }
        if (dto.getEmail().length() > 100) {
            throw new IllegalArgumentException("El email excede 100 caracteres");
        }
        if (dto.getPhoneNumber().length() != 10) {
            throw new IllegalArgumentException("El telefono debe tener 10 caracteres");
        }
        if (!dto.getName().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("El nombre no puede contener cáracteres especiales");
        }
        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El correo debe ser válido");
        }
        if (!dto.getPhoneNumber().matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("El teléfono debe contener solo dígitos numéricos");
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            logger.info("No se encontraron usuarios.");
            return new ResponseEntity<>(new Message("No se encontraron usuarios", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("La búsqueda ha sido realizada correctamente");
        return new ResponseEntity<>(new Message(users, "Lista de usuarios", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findActiveUsers() {
        List<User> activeUsers = userRepository.findByStatus(true);
        if (activeUsers.isEmpty()) {
            logger.info("No se encontraron usuarios activos.");
            return new ResponseEntity<>(new Message("No se encontraron usuarios activos", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("Búsqueda de usuarios activos realizada correctamente");
        return new ResponseEntity<>(new Message(activeUsers, "Lista de usuarios activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    // Método para guardar el usuario
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(UserDto dto) {
        try {
            validateUserData(dto);

            if (userRepository.existsByEmail(dto.getEmail())) {
                return new ResponseEntity<>(new Message("El correo electrónico ya está registrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
                return new ResponseEntity<>(new Message("El número de teléfono ya está registrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            Role role = roleRepository.findById(dto.getRole().getId())
                    .orElseThrow(() -> new IllegalArgumentException("El rol no existe"));

            String encodedPassword = passwordEncoder.encode(dto.getPassword());

            User newUser = new User(
                    dto.getName(),
                    dto.getLastName(),
                    dto.getEmail(),
                    dto.getPhoneNumber(),
                    encodedPassword,
                    role,
                    true
            );

            newUser = userRepository.saveAndFlush(newUser);
            return new ResponseEntity<>(new Message(newUser, "Usuario guardado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al guardar el usuario", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(UserDto dto) {
        try {

            User user = userRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("El usuario no existe"));

            validateUserData(dto);

            if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
                return new ResponseEntity<>(new Message("El correo electrónico ya está registrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            if (!user.getPhoneNumber().equals(dto.getPhoneNumber()) && userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
                return new ResponseEntity<>(new Message("El número de teléfono ya está registrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            Role role = roleRepository.findById(dto.getRole().getId())
                    .orElseThrow(() -> new IllegalArgumentException("El rol no existe"));

            String encodedPassword = dto.getPassword() != null && !dto.getPassword().isEmpty()
                    ? passwordEncoder.encode(dto.getPassword())
                    : user.getPassword();

            user.setName(dto.getName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setPassword(encodedPassword);
            user.setRole(role);

            user = userRepository.saveAndFlush(user);

            return new ResponseEntity<>(new Message(user, "El usuario ha sido actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al actualizar el usuario", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }


    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(UserDto dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("El usuario no existe"));

        user.setStatus(!user.isStatus());

        try {
            user = userRepository.saveAndFlush(user);
            return new ResponseEntity<>(new Message(user, "El estado del usuario ha sido actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al actualizar el estado del usuario", e);
            return new ResponseEntity<>(new Message("Inténtelo de nuevo más tarde", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }
}
