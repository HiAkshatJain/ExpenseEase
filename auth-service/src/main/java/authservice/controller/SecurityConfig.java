//package authservice.controller;
//
//import authservice.repository.UserRepository;
//import authservice.service.UserDetailsServiceImpl;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Slf4j
//@Configuration
//@EnableMethodSecurity
//@Data
//public class SecurityConfig {
//
//    @Autowired
//    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private final UserDetailsServiceImpl userDetailsServiceImpl;
//
////    @Autowired
////    private final UserInfoProducer userInfoProducer;
//
//    @Bean
//    @Autowired
//    public UserDetailsService userDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        return new UserDetailsServiceImpl(userRepository, passwordEncoder, userInfoProducer);
//    }
//
//}
