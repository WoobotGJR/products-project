package com.woobot.springproject.security;

import com.woobot.springproject.entity.Authority;
import com.woobot.springproject.repository.CustomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final CustomUserRepository customUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customUserRepository.findByUsername(username)
                .map(user -> User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        // here we will get exception about lazy loading of Collections (List in here)
                        // error occurs, because links that returns one object are eager (loads immediately)
                        // to handle that, we can make authorities eager (in CustomUser Entity) or make
                        // that method @Transactional, so all of these methods after return will occur in one transaction
                        .authorities(user.getAuthorities().stream()
                                .map(Authority::getAuthority)
                                .map(SimpleGrantedAuthority::new)
                                .toList())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User %s found".formatted(username)));
    }
}
