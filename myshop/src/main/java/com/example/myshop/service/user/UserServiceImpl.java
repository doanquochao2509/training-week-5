package com.example.myshop.service.user;

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
import org.springframework.data.domain.Sort;
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


        if (request.getUsername() == null || request.getFullName() == null ||
                request.getEmail() == null || request.getRoleCode() == null) {
            throw new BadRequestException("Tất cả các trường thông tin bắt buộc không được để trống");
        }


        String username = request.getUsername().trim();
        String fullName = request.getFullName().trim();
        String email    = request.getEmail().trim();
        String roleCode = request.getRoleCode().trim().toUpperCase();


        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || roleCode.isEmpty()) {
            throw new BadRequestException("Thông tin tài khoản không được phép chứa toàn ký tự trống");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BadRequestException("Email không đúng định dạng");
        }

        if (request.getPassword() == null || request.getPassword().trim().length() < 6) {
            throw new BadRequestException("Mật khẩu phải có ít nhất 6 ký tự thực tế (không tính khoảng trắng)");
        }


        if (username.contains(" ")) {
            throw new BadRequestException("Tên đăng nhập viết liền và không được chứa khoảng trắng");
        }


        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Tên đăng nhập này đã tồn tại trên hệ thống");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Địa chỉ Email này đã được đăng ký bởi một tài khoản khác");
        }

        if ("ADMIN".equalsIgnoreCase(roleCode)) {
            throw new BadRequestException("Không được phép tạo tài khoản có quyền Quản trị viên (ADMIN)");
        }

        if (!roleCode.equals("MANAGER") && !roleCode.equals("STAFF")) {
            throw new BadRequestException("Vai trò hợp lệ chỉ bao gồm MANAGER hoặc STAFF");
        }

        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò hệ thống không tồn tại"));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
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

        Pageable pageable = PageRequest.of(page, size,  Sort.by("createdDate").descending());

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

        user.setActive(!user.getActive());

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
    @Override
    public UserResponse updateUser(UUID id, com.example.myshop.dto.user.UpdateUserRequest request) {
        // 1. Tìm kiếm User cần sửa thông tin từ Database
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        // 2. CHỐT CHẶN BYPASS: Tuyệt đối không cho phép sửa đổi tài khoản có quyền ADMIN
        // Điều này bảo vệ các tài khoản Admin không bị hạ cấp hoặc đổi thông tin từ bên ngoài
        if ("ADMIN".equalsIgnoreCase(user.getRole().getCode())) {
            throw new BadRequestException("Không được phép chỉnh sửa thông tin của tài khoản Quản trị viên");
        }

        // 3. Chuẩn hóa dữ liệu đầu vào (Loại bỏ khoảng trắng thừa)
        String fullName = request.getFullName() != null ? request.getFullName().trim() : "";
        String email    = request.getEmail() != null ? request.getEmail().trim() : "";
        String roleCode = request.getRoleCode() != null ? request.getRoleCode().trim().toUpperCase() : "";

        // 4. Kiểm tra tính hợp lệ của các dữ liệu bắt buộc
        if (fullName.isEmpty()) {
            throw new BadRequestException("Họ và tên không được để trống");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BadRequestException("Email không đúng định dạng");
        }

        // 5. Kiểm tra tính hợp lệ của Role mới truyền lên
        if ("ADMIN".equalsIgnoreCase(roleCode)) {
            throw new BadRequestException("Không được phép nâng cấp tài khoản lên vai trò ADMIN");
        }
        if (!roleCode.equals("MANAGER") && !roleCode.equals("STAFF")) {
            throw new BadRequestException("Role chỉ được phép thay đổi thành MANAGER hoặc STAFF");
        }

        // 6. Lấy thực thể Role mới hợp lệ từ DB
        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Vai trò (Role) không tồn tại trên hệ thống"));

        // 7. TIẾN HÀNH GHI ĐÈ (Chỉ ghi đè 3 trường này, trường 'username' trong DB hoàn toàn được giữ nguyên)
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);

        userRepository.save(user);

        // 8. Trả về thông tin phản hồi sạch cho Client
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(role.getCode())
                .active(user.getActive())
                .build();
    }

}
