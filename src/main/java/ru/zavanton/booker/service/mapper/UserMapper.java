package ru.zavanton.booker.service.mapper;

import java.util.*;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import ru.zavanton.booker.domain.Authority;
import ru.zavanton.booker.domain.UserEntity;
import ru.zavanton.booker.service.dto.AdminUserDto;
import ru.zavanton.booker.service.dto.UserDto;

/**
 * Mapper for the entity {@link UserEntity} and its DTO called {@link UserDto}.
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
public class UserMapper {

    public List<UserDto> usersToUserDTOs(List<UserEntity> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToUserDTO).toList();
    }

    public UserDto userToUserDTO(UserEntity user) {
        return new UserDto(user);
    }

    public List<AdminUserDto> usersToAdminUserDTOs(List<UserEntity> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToAdminUserDTO).toList();
    }

    public AdminUserDto userToAdminUserDTO(UserEntity user) {
        return new AdminUserDto(user);
    }

    public List<UserEntity> userDTOsToUsers(List<AdminUserDto> userDTOs) {
        return userDTOs.stream().filter(Objects::nonNull).map(this::userDTOToUser).toList();
    }

    public UserEntity userDTOToUser(AdminUserDto userDTO) {
        if (userDTO == null) {
            return null;
        } else {
            UserEntity user = new UserEntity();
            user.setId(userDTO.getId());
            user.setLogin(userDTO.getLogin());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setImageUrl(userDTO.getImageUrl());
            user.setCreatedBy(userDTO.getCreatedBy());
            user.setCreatedDate(userDTO.getCreatedDate());
            user.setLastModifiedBy(userDTO.getLastModifiedBy());
            user.setLastModifiedDate(userDTO.getLastModifiedDate());
            user.setActivated(userDTO.isActivated());
            user.setLangKey(userDTO.getLangKey());
            Set<Authority> authorities = this.authoritiesFromStrings(userDTO.getAuthorities());
            user.setAuthorities(authorities);
            return user;
        }
    }

    private Set<Authority> authoritiesFromStrings(Set<String> authoritiesAsString) {
        Set<Authority> authorities = new HashSet<>();

        if (authoritiesAsString != null) {
            authorities = authoritiesAsString
                .stream()
                .map(string -> {
                    Authority auth = new Authority();
                    auth.setName(string);
                    return auth;
                })
                .collect(Collectors.toSet());
        }

        return authorities;
    }

    public UserEntity userFromId(Long id) {
        if (id == null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public UserDto toDtoId(UserEntity user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        return userDto;
    }

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public Set<UserDto> toDtoIdSet(Set<UserEntity> users) {
        if (users == null) {
            return Collections.emptySet();
        }

        Set<UserDto> userSet = new HashSet<>();
        for (UserEntity userEntity : users) {
            userSet.add(this.toDtoId(userEntity));
        }

        return userSet;
    }

    @Named("login")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    public UserDto toDtoLogin(UserEntity user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setLogin(user.getLogin());
        return userDto;
    }

    @Named("loginSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    public Set<UserDto> toDtoLoginSet(Set<UserEntity> users) {
        if (users == null) {
            return Collections.emptySet();
        }

        Set<UserDto> userSet = new HashSet<>();
        for (UserEntity userEntity : users) {
            userSet.add(this.toDtoLogin(userEntity));
        }

        return userSet;
    }
}
