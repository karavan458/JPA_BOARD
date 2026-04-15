package com.project.spring_jpa_board.web.dto.member;

import com.project.spring_jpa_board.domain.entity.Address;
import com.project.spring_jpa_board.domain.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberJoinRequest {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 4, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String phone;

    private String city;
    private String street;
    private String zipcode;

    public Member toEntity() {
        return Member.builder()
                .loginId(this.loginId)
                .password(this.password)
                .name(this.name)
                .email(this.email)
                .phone(this.phone)
                .address(new Address(city, street, zipcode))
                .build();
    }
}
