package utez.edu._b.sgc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import utez.edu._b.sgc.users.model.User;
import utez.edu._b.sgc.users.model.UserRepository;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscar usuario por email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // Asignar el rol, usando el prefijo "ROLE_"
        String roleName = "ROLE_" + user.getRole().getName().toUpperCase();  // Usar el nombre del rol directamente

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isStatus(),  // Asegurarse de que el estado del usuario sea considerado en la autenticación
                true, true, true,
                List.of(new SimpleGrantedAuthority(roleName))  // Solo un rol por usuario
        );
    }
}
