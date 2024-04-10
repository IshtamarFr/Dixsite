package fr.ishtamar.starter.controllers;

import fr.ishtamar.starter.model.auth.AuthRequest;
import fr.ishtamar.starter.model.auth.CreateUserRequest;
import fr.ishtamar.starter.model.auth.ModifyUserRequest;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.starter.model.user.UserDto;
import fr.ishtamar.starter.model.user.UserInfo;
import fr.ishtamar.starter.exceptionhandler.BadCredentialsException;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.model.user.UserMapper;
import fr.ishtamar.starter.security.JwtService;
import fr.ishtamar.starter.model.user.UserInfoService;
import fr.ishtamar.starter.model.user.UserInfoServiceImpl;
import fr.ishtamar.starter.util.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserInfoService service;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final EmailService emailSender;

    static final String TOKEN="token";
    @Value("${fr.ishtamar.starter.base-url}")
    private String BASE_URL;

    public AuthController(
            UserInfoServiceImpl service,
            JwtService jwtService,
            UserMapper userMapper,
            EmailService emailSender
    ) {
        this.service=service;
        this.jwtService=jwtService;
        this.userMapper=userMapper;
        this.emailSender=emailSender;
    }

    @Operation(hidden=true)
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @Operation(hidden=true)
    @PostMapping("/sendmemail/{address}")
    @Secured("ROLE_ADMIN")
    public void testSendMail(@PathVariable final String address) {
        emailSender.sendSimpleMessage(
                address,
                "Test message from Dixsite",
                "Sending email from Dixsite WebApp works ! Link to : " + BASE_URL);
    }

    @Operation(summary = "register new user",responses={
            @ApiResponse(responseCode="200", description = "User successfully created"),
            @ApiResponse(responseCode="400", description = "User already exists")
    })
    @PostMapping("/register")
    public String addNewUser(@Valid @RequestBody CreateUserRequest request) throws ConstraintViolationException {
        String token= RandomStringUtils.randomAlphanumeric(15);

        UserInfo userInfo=UserInfo.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles("ROLE_USER")
                .token(token)
                .build();

        Long id=service.createUser(userInfo).getId();
        emailSender.sendValidationLink(request.getEmail(),id,token);
        return "New user successfully created. Waiting for account to be validated";
    }

    @Operation(summary = "logins user and returns JWT",responses={
            @ApiResponse(responseCode="200", description = "Token successfully created"),
            @ApiResponse(responseCode="404", description = "Username not found")
    })
    @PostMapping("/login")
    public Map<String,String> authenticateAndGetToken(@RequestBody @Valid AuthRequest authRequest)
            throws BadCredentialsException,GenericException {
        UserInfo user=service.getUserByUsername(authRequest.getEmail());
        boolean isAuthenticated= BCrypt.checkpw(authRequest.getPassword(),user.getPassword());

        if (user.getToken()==null || user.getToken().length()<15) {
            //User account is validated. We now connect him if password is OK or if the forgotten password token is correct
            if (isAuthenticated || (user.getToken()!=null && authRequest.getPassword().equals(user.getToken()))) {
                Map<String, String> map = new HashMap<>();
                map.put(TOKEN, jwtService.generateToken(authRequest.getEmail()));
                user.setToken(null);
                service.resetUserToken(user);
                return map;
            } else {
                throw new BadCredentialsException();
            }
        } else {
            //User account is not validated. Request is denied
            throw new GenericException("Your account has not been validated yet. Request a new link if needed");
        }
    }

    @Operation(summary = "gets personal data from logged in user",responses={
            @ApiResponse(responseCode="200", description = "Personal data is displayed"),
            @ApiResponse(responseCode="403", description = "Access unauthorized")
    })
    @GetMapping("/me")
    @Secured("ROLE_USER")
    public UserDto userProfile(@RequestHeader(value="Authorization",required=false) String jwt) {
        return userMapper.toDto(service.getUserByUsername(jwtService.extractUsername(jwt.substring(7))));
    }

    @Operation(summary = "changes personal data from logged in user",responses={
            @ApiResponse(responseCode="200", description = "Personal data is changed, new JWT is displayed"),
            @ApiResponse(responseCode="400", description = "Email is already used or password is not valid"),
            @ApiResponse(responseCode="403", description = "Access unauthorized")
    })
    @PutMapping("/me")
    @Secured("ROLE_USER")
    public Map<String,String> userModifyProfile(
            @RequestHeader(value="Authorization",required=false) String jwt,
            @Valid @RequestBody ModifyUserRequest request
    ) throws EntityNotFoundException, BadCredentialsException, ConstraintViolationException {
        UserInfo candidate=service.modifyUser(jwtService.extractUsername(jwt.substring(7)),request);

        //prepare a new JWT to show (not executed if there's an error before)
        Map<String,String>map=new HashMap<>();
        map.put(TOKEN,jwtService.generateToken(candidate.getEmail()));

        return map;
    }

    @Operation(summary = "new user tries to validate their account",responses={
            @ApiResponse(responseCode="200", description = "Account is validated"),
            @ApiResponse(responseCode="400", description = "Id-Token is invalid, or account already validated"),
            @ApiResponse(responseCode="404", description = "User is not found")
    })
    @PutMapping("/validate")
    public Map<String,String> validateNewUser(
            @RequestParam final Long id,
            @RequestParam final String token
    ) throws EntityNotFoundException, BadCredentialsException,GenericException {
        UserInfo user=service.getUserById(id);
        if (user.getToken()!=null) {
            if (user.getToken().equals(token)) {
                service.validateUser(user);

                Map<String, String> map = new HashMap<>();
                map.put(TOKEN, jwtService.generateToken(user.getEmail()));
                return map;
            } else {
                throw new BadCredentialsException();
            }
        } else {
            throw new GenericException("This account has already be activated");
        }
    }

    @Operation(summary = "user requires a temporary password token",responses={
            @ApiResponse(responseCode="200", description = "token has been sent to email"),
            @ApiResponse(responseCode="404", description = "User is not found")
    })
    @PostMapping("/forgotten")
    public String forgottenPassword(
            @RequestParam final String email
    ) throws EntityNotFoundException, GenericException {
        UserInfo user=service.getUserByUsername(email);

        if (user.getToken()==null || user.getToken().length()<15){
            //User account is validated, we can serve a new temporary password
            String token= RandomStringUtils.randomAlphanumeric(10);
            user.setToken(token);
            service.modifyUser(user);
            emailSender.sendTemporaryPassword(user.getEmail(),token);
            return "A temporary password has been sent if this address exists";
        }else {
            //User account is not validated, we can serve a new validation link
            String token= RandomStringUtils.randomAlphanumeric(15);
            user.setToken(token);
            service.modifyUser(user);
            emailSender.sendValidationLink(user.getEmail(),user.getId(),token);
            return "A new link for validation has been sent";
        }
    }
}
