package com.akn.ns.neighbour_snack_be.service;

import com.akn.ns.neighbour_snack_be.dto.UnVerifiedUserDto;
import com.akn.ns.neighbour_snack_be.dto.UnVerifiedUserDto.UnVerifiedUserPasswordRequestDto;
import com.akn.ns.neighbour_snack_be.dto.UnVerifiedUserDto.UnVerifiedUserPublicResponseDto;

import java.util.UUID;

public interface UnVerifiedUserService {

    UnVerifiedUserPublicResponseDto registerUnVerifiedUser(UnVerifiedUserDto.UnVerifiedUserRequestDto unVerifiedUserRequestDTO);

    UnVerifiedUserPublicResponseDto verifyLink(UUID uuid);

    void verifyAndSaveUser(UUID uuid, UnVerifiedUserPasswordRequestDto unVerifiedUserPasswordDTO);

    void cleanUpExpiredUnverifiedUsers();

}
