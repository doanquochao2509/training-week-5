package com.example.myshop.service.customer;

import com.example.myshop.dto.customer.*;
import com.example.myshop.entity.Customer;
import com.example.myshop.exception.BadRequestException;
import com.example.myshop.exception.ResourceNotFoundException;
import com.example.myshop.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    // Biểu thức Regex kiểm tra định dạng Email và Số điện thoại Việt Nam
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^(0|\\+84)[35789]\\d{8}$");

    @Override
    public CustomerResponse create(CreateCustomerRequest request) {
        // 1. Validate các trường bắt buộc và định dạng (Email, Phone)
        if (request.getCustomerCode() == null || request.getCustomerCode().trim().isEmpty()) {
            throw new BadRequestException("Mã khách hàng không được để trống");
        }

        validateCustomerData(request.getCustomerName(), request.getPhone(), request.getEmail());

        // 2. Kiểm tra trùng mã code
        if (customerRepository.existsByCustomerCode(request.getCustomerCode())) {
            throw new BadRequestException("Mã khách hàng đã tồn tại");
        }

        Customer customer = new Customer();
        customer.setCustomerCode(request.getCustomerCode().trim());
        customer.setCustomerName(request.getCustomerName().trim());
        customer.setPhone(request.getPhone().trim());
        customer.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
        customer.setAddress(request.getAddress());
        customer.setActive(true);

        customerRepository.save(customer);
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse update(UUID id, UpdateCustomerRequest request) {
        // 1. Validate định dạng dữ liệu sửa đổi
        validateCustomerData(request.getCustomerName(), request.getPhone(), request.getEmail());

        // 2. Kiểm tra tồn tại
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại"));

        customer.setCustomerName(request.getCustomerName().trim());
        customer.setPhone(request.getPhone().trim());
        customer.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
        customer.setAddress(request.getAddress());

        customerRepository.save(customer);
        return mapToResponse(customer);
    }

    /**
     * Hàm dùng chung để kiểm tra tính hợp lệ của dữ liệu Khách hàng tại tầng Service
     */
    private void validateCustomerData(String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Tên khách hàng không được để trống");
        }

        if (phone == null || phone.trim().isEmpty()) {
            throw new BadRequestException("Số điện thoại không được để trống");
        }
        if (!PHONE_REGEX.matcher(phone.trim()).matches()) {
            throw new BadRequestException(
                    "Số điện thoại không đúng định dạng. Ví dụ hợp lệ: 0987654321 hoặc 0371234567."
            );
        }

        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_REGEX.matcher(email.trim()).matches()) {
                throw new BadRequestException("Email không đúng định dạng (ví dụ: abc@domain.com)");
            }
        }
    }

    @Override
    public void changeStatus(UUID id, Boolean active) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại"));
        customer.setActive(active);
        customerRepository.save(customer);
    }

    @Override
    public CustomerResponse detail(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại"));
        return mapToResponse(customer);
    }

    @Override
    public Page<CustomerResponse> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return customerRepository.search(keyword, pageable).map(this::mapToResponse);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .customerName(customer.getCustomerName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .active(customer.getActive())
                .build();
    }
}