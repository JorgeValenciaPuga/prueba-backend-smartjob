package com.smartjob.creacionusuarios.service;

import com.smartjob.creacionusuarios.domain.Phone;
import com.smartjob.creacionusuarios.domain.User;
import com.smartjob.creacionusuarios.domain.UserRequestDTO;
import com.smartjob.creacionusuarios.domain.UserResponseDTO;
import com.smartjob.creacionusuarios.exception.UserException;
import com.smartjob.creacionusuarios.repository.UserRepository;
import com.smartjob.creacionusuarios.security.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ValidatorService validatorService;

    @Autowired
    JwtService jwtService;

    public UserService(UserRepository userRepository, ValidatorService validatorService) {
        this.userRepository = userRepository;
        this.validatorService = validatorService;
    }

    @Transactional
    public UserResponseDTO registrarUsuario(UserRequestDTO request) {

        try{
            Optional<User> usuarioExistente = userRepository.findByEmail(request.getEmail());

            validatorService.validarCorreoExistente(usuarioExistente);

            validatorService.validarCorreo(request.getEmail());

            validatorService.validarClave(request.getPassword());

            String claveEncriptada = passwordEncoder.encode(request.getPassword());

            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(claveEncriptada);
            user.setCreated(LocalDateTime.now());

            List<Phone> telefonos = request.getPhones().stream()
                    .map(t -> new Phone(t.getNumber(), t.getCitycode(), t.getCountrycode(), user))
                    .toList();
            user.setPhones(telefonos);

            String token = jwtService.generarToken(user);

            user.setToken(token);

            userRepository.save(user);

            return new UserResponseDTO(user.getId(),user.getName(),user.getEmail(),user.getCreated(),LocalDateTime.now(),LocalDateTime.now(),token,true);
        }catch(UserException e){
            System.out.println("Error:" + e);
            throw e;
        }
        
    }
    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

}