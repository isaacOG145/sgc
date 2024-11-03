package utez.edu._b.sgc.users.control;


import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu._b.sgc.projectCat.model.ProjectCatDTO;
import utez.edu._b.sgc.projectCat.model.ProjectCategory;
import utez.edu._b.sgc.role.model.Role;
import utez.edu._b.sgc.role.model.RoleRepository;
import utez.edu._b.sgc.users.model.User;
import utez.edu._b.sgc.users.model.UserDto;
import utez.edu._b.sgc.users.model.UserRepository;
import utez.edu._b.sgc.utils.Message;
import utez.edu._b.sgc.utils.TypesResponse;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository repository){
        this.userRepository = repository;
    }

    //validar datos
    private void validateUserData(UserDto dto) {
        if (dto.getName().length() > 50) {
            throw new IllegalArgumentException("El nombre excede 50 caracteres");
        }
        if(dto.getLastName().length() > 100) {
            throw new IllegalArgumentException("El apellido excede 100 caracteres");
        }
        if(dto.getUserName().length() > 30) {
            throw new IllegalArgumentException("El nombre excede 30 caracteres");
        }
        if (dto.getEmail().length() > 100 ){
            throw new IllegalArgumentException("El email excede 100 caracteres");
        }
        if(dto.getPhoneNumber().length() > 10) {
            throw new IllegalArgumentException("El telefono excede 10 caracteres");
        }

    }

    //Encontrar todo
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<User> users = userRepository.findAll();
        logger.info("La búsqueda ha sido realizada correctamente");
        return new ResponseEntity<>(new Message(users,"Lista de las usuarios", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Emcontrar todos los activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findActiveUsers() {
        List<User> activeUsers = userRepository.findByStatus(true);
        logger.info("Búsqueda de las categorías de usuarios activos realizada correctamente");
        return new ResponseEntity<>(new Message(activeUsers, "Lista de las usuarios activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Guardar cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(UserDto dto) {
        try {
            validateUserData(dto); // Llama al método de validación

            User newUser = new User(
                    dto.getName(),
                    dto.getLastName(),
                    dto.getUserName(),
                    dto.getEmail(),
                    dto.getPhoneNumber(),
                    dto.getPassword(),
                    true
                    //roles queda pendiente
                    );
            newUser = userRepository.saveAndFlush(newUser);

            return new ResponseEntity<>(new Message(newUser, "Usuario guardado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Maneja las excepciones lanzadas por la validación
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error al guardar usuario", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    //actualizar cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(UserDto dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("El usuario no existe"));

        validateUserData(dto);

        user.setName(dto.getName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(dto.getPassword());

        try {
            user = userRepository.saveAndFlush(user);
            return new ResponseEntity<>(new Message(user, "El usuario a sido actualizada exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al actualizar el usuario", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    //cambiar estado
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(UserDto dto) {
        // Buscar el cliente por ID, lanzando una excepción si no se encuentra
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("El usuario no existe"));

        user.setStatus(!user.isStatus());

        try {
            user = userRepository.saveAndFlush(user);
            return new ResponseEntity<>(new Message(user, "El estado del usuario a sido actualizado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al actualizar el estado del usuario", e);
            return new ResponseEntity<>(new Message("Intentelo denuevo mas tarde", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

}
