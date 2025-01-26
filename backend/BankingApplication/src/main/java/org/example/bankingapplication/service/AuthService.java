package org.example.bankingapplication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.bankingapplication.dto.account.AccountRequestDTO;
import org.example.bankingapplication.dto.login.LoginRequestDTO;
import org.example.bankingapplication.dto.login.LoginResponseDTO;
import org.example.bankingapplication.dto.register.RegisterRequestDTO;
import org.example.bankingapplication.dto.register.RegisterResponseDTO;
import org.example.bankingapplication.enums.Roles;
import org.example.bankingapplication.exceptions.UserAlreadyExistsException;
import org.example.bankingapplication.exceptions.UserAlreadyLoggedInException;
import org.example.bankingapplication.model.User;
import org.example.bankingapplication.repository.AccountRepository;
import org.example.bankingapplication.repository.TokenRepository;
import org.example.bankingapplication.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final Argon2PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) throws UserAlreadyExistsException {
        if (userRepository.existsUserByEmail(registerRequestDTO.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists!");
        }
        if (userRepository.existsUserByUsername(registerRequestDTO.getUsername())) {
            throw new UserAlreadyExistsException("This username already exists!");
        }

        String accountNumber = generateAccountNumber();
        if (userRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = generateAccountNumber();
        }

        User user = User.builder()
                .username(registerRequestDTO.getUsername())
                .email(registerRequestDTO.getEmail())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .role(Roles.ROLE_USER)
                .accountNumber(accountNumber)
                .build();
        userRepository.save(user);

        AccountRequestDTO accountRequestDTO = AccountRequestDTO.builder()
                .userId(user.getId())
                .accountNumber(accountNumber)
                .initialDeposit(0.0)
                .build();

        accountService.createAccount(accountRequestDTO);

        return new RegisterResponseDTO(user.getId(), "User registered successfully!");
    }


    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));

        User user = userRepository.findUserByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var validUserTokens = tokenService.getAllValidUserTokens(user);
        if (!validUserTokens.isEmpty()) {
            throw new UserAlreadyLoggedInException("You are already logged in");
        }

        tokenService.deleteAllUserTokens(user);
        String jwtToken = jwtService.generateToken(user);
        tokenService.saveUserToken(user, jwtToken);

        return LoginResponseDTO.builder()
                .token(jwtToken)
                .message("User logged in successfully")
                .build();
    }

    public static String generateAccountNumber() {
        String countryCode = "LT";
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder(countryCode);
        for (int i = 0; i < 14; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }

    public Optional<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findUserByUsername(username);
        }
        return Optional.empty();
    }
}
