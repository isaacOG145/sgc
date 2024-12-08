package utez.edu._b.sgc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import utez.edu._b.sgc.security.dto.AuthRequest;
import utez.edu._b.sgc.security.dto.AuthResponse;
import utez.edu._b.sgc.users.control.UserService;
import utez.edu._b.sgc.users.model.User;
import utez.edu._b.sgc.users.model.UserDto;
import utez.edu._b.sgc.users.model.UserRepository;
import utez.edu._b.sgc.utils.Message;

@RestController
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, UserRepository userRepository, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Correo o contraseña incorrectos", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

        final String jwt = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        long expirationTime = jwtUtil.getExpirationTime();


        return new AuthResponse(jwt, user.getId(), user.getEmail(),user.getRole().getName(),expirationTime);
    }

    @PutMapping("/verify-code")
    public ResponseEntity<AuthResponse> verifyCodeAndGenerateToken(@RequestBody UserDto dto) {
        try {

            ResponseEntity<Message> verifyResponse = userService.verifyCode(dto);

            if (verifyResponse.getStatusCode() == HttpStatus.OK) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());

                String token = jwtUtil.generateToken(userDetails);
                long expirationTime = jwtUtil.getExpirationTime();

                AuthResponse authResponse = new AuthResponse(token, dto.getId(), dto.getEmail(),dto.getRole().getName(), expirationTime);

                return new ResponseEntity<>(authResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}