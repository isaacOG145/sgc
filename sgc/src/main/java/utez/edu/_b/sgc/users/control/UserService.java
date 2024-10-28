package utez.edu._b.sgc.users.control;


import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu._b.sgc.role.model.Role;
import utez.edu._b.sgc.role.model.RoleRepository;
import utez.edu._b.sgc.users.model.User;
import utez.edu._b.sgc.users.model.UserDto;
import utez.edu._b.sgc.users.model.UserRepository;
import utez.edu._b.sgc.utils.Message;
import utez.edu._b.sgc.utils.TypesResponse;

import java.sql.SQLException;
import java.util.HashSet;
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

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll() {
        return new ResponseEntity<>(new Message(userRepository.findAll(), "Listado de estados de usuarios activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAllEnabled() {
        return new ResponseEntity<>(new Message(userRepository.findAllByStatusOrderByName(true), "Listado de usuarios de residencia activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Guardar
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> save(UserDto dto) {
        if (dto.getName().length() > 50) {
            return new ResponseEntity<>(new Message("El nombre excede el número de caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getLastName().length() > 100) {
            return new ResponseEntity<>(new Message("Los apellidos exceden el número de caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getEmail().length() > 100) {
            return new ResponseEntity<>(new Message("El correo excede el número de caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPhoneNumber().length() > 10) {
            return new ResponseEntity<>(new Message("El número excede el número de caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPassword().length() < 8) {
            return new ResponseEntity<>(new Message("La contraseña no cumple el mínimo de caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        Set<Role> roles = new HashSet<>();
        for (Long roleId : dto.getRoleIds()) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + roleId));
            roles.add(role);
        }

        User user = new User();
        user.setName(dto.getName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(dto.getPassword());
        user.setStatus(true);
        user.setRoles(roles);

        user = userRepository.saveAndFlush(user);

        if (user == null) {
            logger.error("Error al guardar el usuario");
            return new ResponseEntity<>(new Message("No se registró el usuario", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new Message(user, "Usuario registrado con éxito", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //update pendiente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> update(UserDto dto){
        return null;
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> changeStatus(UserDto dto) {
        Optional<User> optional = userRepository.findById(dto.getId());
        if (!optional.isPresent()) {
            return new ResponseEntity<>(new Message("No se encontró el usuario", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        User user = optional.get();
        user.setStatus(!user.isStatus());
        user = userRepository.saveAndFlush(user);
        if (user == null) {
            logger.error("No se pudo modificar el Estado del usuario");
            return new ResponseEntity<>(new Message("No se modificó el estado del usuario", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(user, "Se modificó el estado del usuario", TypesResponse.SUCCESS), HttpStatus.OK);
    }


}
