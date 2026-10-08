package com.example.webframework.user;

import com.example.webframework.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user-account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    // Create
    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@Valid @RequestBody SignUpRequest request) {
        Long id = userAccountService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return userAccountService.checkEmail(email);
    }

    // Read - 단건
    @GetMapping("/{email}")
    public ResponseEntity<UserAccountResponse> getUser(@PathVariable String email) {
        return ResponseEntity.ok(userAccountService.getUser(email));
    }

    // Read - 전체
    @GetMapping
    public ResponseEntity<List<UserAccountResponse>> getAllUsers() {
        return ResponseEntity.ok(userAccountService.getAllUsers());
    }

    // Update
    @PatchMapping("/update")
    public ResponseEntity<UserAccountResponse> updateUser(
            @RequestParam String email,
            @RequestBody UserAccountUpdateRequest request
    ) {
        return ResponseEntity.ok(userAccountService.updateUser(email, request));
    }

    // Delete
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestParam String email) {
        userAccountService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }
}
