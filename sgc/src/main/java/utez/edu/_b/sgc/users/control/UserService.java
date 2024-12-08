package utez.edu._b.sgc.users.control;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu._b.sgc.customer.model.Customer;
import utez.edu._b.sgc.role.model.Role;
import utez.edu._b.sgc.role.model.RoleRepository;
import utez.edu._b.sgc.security.JwtUtil;
import utez.edu._b.sgc.security.UserDetailsServiceImpl;
import utez.edu._b.sgc.security.dto.AuthResponse;
import utez.edu._b.sgc.users.model.User;
import utez.edu._b.sgc.users.model.UserDto;
import utez.edu._b.sgc.users.model.UserRepository;
import utez.edu._b.sgc.utils.EmailSender;
import utez.edu._b.sgc.utils.Message;
import utez.edu._b.sgc.utils.TypesResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailSender emailSender;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, EmailSender emailSender, JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailSender = emailSender;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
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
        if (!dto.getPhoneNumber().matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("El teléfono debe contener solo dígitos numéricos");
        }
        if (dto.getPhoneNumber().length() != 10) {
            throw new IllegalArgumentException("El telefono debe tener 10 caracteres");
        }
        if (!dto.getName().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("El nombre no puede contener cáracteres especiales");
        }
        if (!dto.getLastName().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("El nombre no puede contener cáracteres especiales");
        }
        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El correo debe ser válido");
        }


    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> getUserById(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            // Si el usuario es encontrado, se responde con sus datos.
            return new ResponseEntity<>(new Message(user, "Datos del usuario obtenidos exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("Usuario no encontrado", e);
            return new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error al obtener los datos del usuario", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
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
                    true,
                    false
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

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> sendEmail(UserDto dto) {
        Optional<User> optional = userRepository.findFirstByEmail(dto.getEmail());
        if(!optional.isPresent()){
            return new ResponseEntity<>(new Message("Correo no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }

        Random random = new Random();
        StringBuilder numberString = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            int digit = random.nextInt(10);
            numberString.append(digit);
        }

        User user = optional.get();
        user.setCode(numberString.toString());
        user = userRepository.saveAndFlush(user);
        if(user == null){
            return new ResponseEntity<>(new Message("Código no registrado", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }

        emailSender.sendSimpleMessage(user.getEmail(),
                "Sistema Prueba | Solicitud de restablecimiento de contraseña",
                "<!DOCTYPE html>\n" +
                        "<html xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "\t<title></title>\n" +
                        "\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                        "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml><![endif]--><!--[if !mso]><!-->\n" +
                        "\t<link href=\"https://fonts.googleapis.com/css?family=Cabin\" rel=\"stylesheet\" type=\"text/css\"><!--<![endif]-->\n" +
                        "\t<style>\n" +
                        "\t\t* {\n" +
                        "\t\t\tbox-sizing: border-box;\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\t\tbody {\n" +
                        "\t\t\tmargin: 0;\n" +
                        "\t\t\tpadding: 0;\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\t\ta[x-apple-data-detectors] {\n" +
                        "\t\t\tcolor: inherit !important;\n" +
                        "\t\t\ttext-decoration: inherit !important;\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\t\t#MessageViewBody a {\n" +
                        "\t\t\tcolor: inherit;\n" +
                        "\t\t\ttext-decoration: none;\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\t\tp {\n" +
                        "\t\t\tline-height: inherit\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\t\t.desktop_hide,\n" +
                        "\t\t.desktop_hide table {\n" +
                        "\t\t\tmso-hide: all;\n" +
                        "\t\t\tdisplay: none;\n" +
                        "\t\t\tmax-height: 0px;\n" +
                        "\t\t\toverflow: hidden;\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\t\t.image_block img+div {\n" +
                        "\t\t\tdisplay: none;\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\t\tsup,\n" +
                        "\t\tsub {\n" +
                        "\t\t\tfont-size: 75%;\n" +
                        "\t\t\tline-height: 0;\n" +
                        "\t\t}\n" +
                        "\n" +
                        "\t\t@media (max-width:670px) {\n" +
                        "\t\t\t.image_block div.fullWidth {\n" +
                        "\t\t\t\tmax-width: 100% !important;\n" +
                        "\t\t\t}\n" +
                        "\n" +
                        "\t\t\t.mobile_hide {\n" +
                        "\t\t\t\tdisplay: none;\n" +
                        "\t\t\t}\n" +
                        "\n" +
                        "\t\t\t.row-content {\n" +
                        "\t\t\t\twidth: 100% !important;\n" +
                        "\t\t\t}\n" +
                        "\n" +
                        "\t\t\t.stack .column {\n" +
                        "\t\t\t\twidth: 100%;\n" +
                        "\t\t\t\tdisplay: block;\n" +
                        "\t\t\t}\n" +
                        "\n" +
                        "\t\t\t.mobile_hide {\n" +
                        "\t\t\t\tmin-height: 0;\n" +
                        "\t\t\t\tmax-height: 0;\n" +
                        "\t\t\t\tmax-width: 0;\n" +
                        "\t\t\t\toverflow: hidden;\n" +
                        "\t\t\t\tfont-size: 0px;\n" +
                        "\t\t\t}\n" +
                        "\n" +
                        "\t\t\t.desktop_hide,\n" +
                        "\t\t\t.desktop_hide table {\n" +
                        "\t\t\t\tdisplay: table !important;\n" +
                        "\t\t\t\tmax-height: none !important;\n" +
                        "\t\t\t}\n" +
                        "\t\t}\n" +
                        "\t</style><!--[if mso ]><style>sup, sub { font-size: 100% !important; } sup { mso-text-raise:10% } sub { mso-text-raise:-10% }</style> <![endif]-->\n" +
                        "</head>\n" +
                        "\n" +
                        "<body class=\"body\" style=\"background-color: #000000; margin: 0; padding: 0; -webkit-text-size-adjust: none; text-size-adjust: none;\">\n" +
                        "\t<table class=\"nl-container\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #000000;\">\n" +
                        "\t\t<tbody>\n" +
                        "\t\t\t<tr>\n" +
                        "\t\t\t\t<td>\n" +
                        "\t\t\t\t\t<table class=\"row row-1\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f3e6f8;\">\n" +
                        "\t\t\t\t\t\t<tbody>\n" +
                        "\t\t\t\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t\t\t\t<td>\n" +
                        "\t\t\t\t\t\t\t\t\t<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #ffffff; background-image: url('https://d1oco4z2z1fhwp.cloudfront.net/templates/default/2971/ResetPassword_BG_2.png'); background-position: center top; background-repeat: no-repeat; color: #000000; width: 650px; margin: 0 auto;\" width=\"650\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t<tbody>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; padding-top: 45px; vertical-align: top; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"divider_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"20\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"alignment\" align=\"center\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"divider_inner\" style=\"font-size: 1px; line-height: 1px; border-top: 0px solid #BBBBBB;\"><span style=\"word-break: break-word;\">&#8202;</span></td>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"image_block block-2\" width=\"100%\" border=\"0\" cellpadding=\"20\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"alignment\" align=\"center\" style=\"line-height:10px\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"fullWidth\" style=\"max-width: 357.5px;\"><img src=\"https://d1oco4z2z1fhwp.cloudfront.net/templates/default/2971/lock5.png\" style=\"display: block; height: auto; border: 0; width: 100%;\" width=\"357.5\" alt=\"Forgot your password?\" title=\"Forgot your password?\" height=\"auto\"></div>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"heading_block block-3\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\" style=\"padding-top:35px;text-align:center;width:100%;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<h1 style=\"margin: 0; color: #8412c0; direction: ltr; font-family: 'Cabin', Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 28px; font-weight: 400; letter-spacing: normal; line-height: 120%; text-align: center; margin-top: 0; margin-bottom: 0; mso-line-height-alt: 33.6px;\"><strong>¿Olvidaste tu contraseña? Este es tú código:</strong></h1>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"paragraph_block block-4\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\" style=\"padding-left:45px;padding-right:45px;padding-top:10px;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div style=\"color:#393d47;font-family:'Cabin',Arial,'Helvetica Neue',Helvetica,sans-serif;font-size:36px;line-height:200%;text-align:center;mso-line-height-alt:72px;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p style=\"margin: 0; word-break: break-word;\"><span style=\"word-break: break-word; color: #aa67cf;\">"+user.getCode()+"</span></p>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t<table class=\"paragraph_block block-5\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td class=\"pad\" style=\"padding-bottom:20px;padding-left:10px;padding-right:10px;padding-top:10px;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div style=\"color:#8412c0;font-family:Arial, Helvetica Neue, Helvetica, sans-serif;font-size:14px;line-height:120%;text-align:center;mso-line-height-alt:16.8px;\">\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p style=\"margin: 0; word-break: break-word;\"><span style=\"word-break: break-word; color: #8a3b8f;\">Sistema prueba ©</span></p>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                        "\t\t\t\t\t\t\t\t\t\t</tbody>\n" +
                        "\t\t\t\t\t\t\t\t\t</table>\n" +
                        "\t\t\t\t\t\t\t\t</td>\n" +
                        "\t\t\t\t\t\t\t</tr>\n" +
                        "\t\t\t\t\t\t</tbody>\n" +
                        "\t\t\t\t\t</table>\n" +
                        "\t\t\t\t</td>\n" +
                        "\t\t\t</tr>\n" +
                        "\t\t</tbody>\n" +
                        "\t</table>" +
                        "</body>\n" +
                        "\n" +
                        "</html>");

        return new ResponseEntity<>(new Message("Correo enviado", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Message> verifyCode(UserDto dto) {
        try {
            User user = userRepository.findFirstByEmailAndCode(dto.getEmail(), dto.getCode())
                    .orElseThrow(() -> new RuntimeException("No se pudo verificar el código. El correo o el código pueden ser incorrectos."));

            if (user.isVerified()) {
                return new ResponseEntity<>(new Message("Este código ya ha sido utilizado o el usuario ya está verificado.", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            user.setVerified(true);
            user = userRepository.saveAndFlush(user);

            return new ResponseEntity<>(new Message(user, "Código verificado correctamente.", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (IllegalArgumentException e) {

            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Capturar cualquier otro error
            logger.error("Error al verificar el código", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo.", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional
    public ResponseEntity<Message> ChangePassword(UserDto dto) {
        try {
            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new RuntimeException("El usuario no existe"));

            if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                return new ResponseEntity<>(new Message("La nueva contraseña no puede ser la misma que la anterior", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            String encodedPassword = dto.getPassword() != null && !dto.getPassword().isEmpty()
                    ? passwordEncoder.encode(dto.getPassword())
                    : user.getPassword();

            user.setPassword(encodedPassword);
            user.setVerified(false);
            user.setCode(null);

            user = userRepository.saveAndFlush(user);

            return new ResponseEntity<>(new Message(user, "La contraseña y el código se han actualizado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (IllegalArgumentException e) {

            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            logger.error("Error al actualizar la contraseña y el código", e);
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }



}
