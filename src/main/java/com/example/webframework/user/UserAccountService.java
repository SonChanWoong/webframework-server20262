package com.example.webframework.user;

import com.example.webframework.user.dto.SignUpRequest;
import com.example.webframework.user.dto.UserAccountResponse;
import com.example.webframework.user.dto.UserAccountUpdateRequest;
import com.example.webframework.auth.JwtService;
import com.example.webframework.auth.dto.LoginRequest;
import com.example.webframework.auth.dto.LoginResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        UserAccount userAccount = userAccountRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), userAccount.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtService.createAccessToken(userAccount);
        return new LoginResponse(accessToken, "Bearer", jwtService.getAccessTokenExpirationSeconds(),
                UserAccountResponse.from(userAccount, "로그인되었습니다."));
    }

    public Long signUp(SignUpRequest request) {

        // 이메일, 닉네임 이미 디비에 있는지 체크
        // 체크했는데 이미 있으면 에러

        if (userAccountRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."
            );
        }

        if (userAccountRepository.existsByNickname(request.nickname())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."
            );
        }

        String passwordHash = passwordEncoder.encode(request.password());


        UserAccount userAccount = new UserAccount(request.email(), passwordHash, request.nickname());
        UserAccount saved = userAccountRepository.save(userAccount);

        return saved.getId();
    }

    public boolean checkEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    // Read - 단건
    public UserAccountResponse getUser(String email) {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. email=" + email));
        return UserAccountResponse.from(userAccount, "계정 정보가 변경되었습니다.");
    }

    // Read - 전체
    public List<UserAccountResponse> getAllUsers() {
        return userAccountRepository.findAll().stream()
                .map(userAccount -> UserAccountResponse.from(userAccount, "계정 정보가 변경되었습니다."))
                .toList();
    }

    // Update
    public UserAccountResponse updateUser(String email, UserAccountUpdateRequest request) {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. email=" + email));

        if (!userAccount.getEmail().equals(request.email())
                && userAccountRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + request.email());
        }

        String passwordHash = passwordEncoder.encode(request.passwordHash());
        userAccount.changeEmail(request.email());
        userAccount.changeNickname(request.nickname());
        userAccount.changePasswordHash(passwordHash);

        return UserAccountResponse.from(userAccount, "계정 정보가 변경되었습니다.");
    }

    // Delete
    public void deleteUser(String email) {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. email=" + email));
        userAccountRepository.delete(userAccount);
    }
}
