package net.noahk.takenlijst.services;

import net.noahk.takenlijst.dtos.AuthDto;
import net.noahk.takenlijst.dtos.UserDto;
import net.noahk.takenlijst.exceptions.UnmetPreconditionException;
import net.noahk.takenlijst.models.Role;
import net.noahk.takenlijst.models.User;
import net.noahk.takenlijst.repositories.RoleRepository;
import net.noahk.takenlijst.repositories.UserRepository;
import net.noahk.takenlijst.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public String getToken(AuthDto auth) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(auth.username, auth.password);
        var authentication = authenticationManager.authenticate(authenticationToken);

        var userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails);
    }

    public void createUser(UserDto user) throws UnmetPreconditionException {
        User toSave = new User();
        toSave.setUsername(user.username);
        toSave.setPassword(passwordEncoder.encode(user.password));

        List<Role> userRoles = new ArrayList<>();
        for (String rolename : user.roles) {
            Optional<Role> role = roleRepository.findById(rolename);

            if (role.isEmpty()) {
                throw new UnmetPreconditionException("Role does not exist!");
            }

            userRoles.add(role.get());
        }
        toSave.setRoles(userRoles);

        userRepository.save(toSave);
    }

}
