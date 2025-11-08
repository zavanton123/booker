package ru.zavanton.booker.service.dto;

import java.io.Serializable;
import java.util.Objects;
import ru.zavanton.booker.domain.UserEntity;

/**
 * A DTO representing a user, with only the public attributes.
 */
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String login;

    public UserDto() {
        // Empty constructor needed for Jackson.
    }

    public UserDto(UserEntity user) {
        this.id = user.getId();
        // Customize it here if you need, or not, firstName/lastName/etc
        this.login = user.getLogin();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserDto userDTO = (UserDto) o;
        if (userDTO.getId() == null || getId() == null) {
            return false;
        }

        return Objects.equals(getId(), userDTO.getId()) && Objects.equals(getLogin(), userDTO.getLogin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLogin());
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDto{" +
            "id='" + id + '\'' +
            ", login='" + login + '\'' +
            "}";
    }
}
