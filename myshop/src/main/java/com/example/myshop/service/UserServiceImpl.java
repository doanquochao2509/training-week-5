package com.example.myshop.service;

import com.example.myshop.dto.user.CreateUserRequest;
import com.example.myshop.dto.user.UserResponse;
import com.example.myshop.entity.Role;
import com.example.myshop.entity.User;
import com.example.myshop.exception.BadRequestException;
import com.example.myshop.exception.ResourceNotFoundException;
import com.example.myshop.repository.RoleRepository;
import com.example.myshop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        // Trim tất cả các trường trước khi xử lý
        String username = request.getUsername().trim();
        String fullName = request.getFullName().trim();
        String email    = request.getEmail().trim();
        String roleCode = request.getRoleCode().trim().toUpperCase();

        // Kiểm tra username có khoảng trắng ở giữa không
        if (username.contains(" ")) {
            throw new BadRequestException(
                    "Tên đăng nhập không được chứa khoảng trắng");
        }

        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException(
                    "Tên đăng nhập đã tồn tại");
        }

        if ("ADMIN".equalsIgnoreCase(roleCode)) {
            throw new BadRequestException(
                    "Không được phép tạo tài khoản ADMIN");
        }

        if (!roleCode.equals("MANAGER") && !roleCode.equals("STAFF")) {
            throw new BadRequestException(
                    "Role chỉ được phép là MANAGER hoặc STAFF");
        }

        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role không tồn tại"));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setActive(true);

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(role.getCode())
                .active(user.getActive())
                .build();
    }
    @Override
    public Page<UserResponse> getUsers(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findAll(pageable)
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .role(u.getRole().getCode())
                        .active(u.getActive())
                        .build());
    }
    @Override
    public void disableUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy tài khoản"));

        user.setActive(false);

        userRepository.save(user);
    }
    @Override
    public void resetPassword(
            UUID id,
            String password) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy tài khoản"));

        user.setPassword(
                passwordEncoder.encode(password));

        userRepository.save(user);
    }

}
