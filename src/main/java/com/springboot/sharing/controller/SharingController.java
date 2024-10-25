package com.springboot.sharing.controller;

import com.springboot.dream.entity.Dream;
import com.springboot.dream.service.DreamService;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.dto.MemberRewardPictureDto;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.sharing.dto.SharingDto;
import com.springboot.sharing.mapper.SharingMapper;
import com.springboot.sharing.service.SharingService;

import com.springboot.sharing.entity.Sharing;
import com.springboot.stamp.service.StampService;
import com.springboot.utils.UriCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
@RestController
@RequestMapping("/dreams/{dreamId}/sharing")
@Validated
@Slf4j
public class SharingController {

    private final SharingService sharingService;
    SharingMapper sharingMapper;
    private final DreamService dreamService;
    private final StampService stampService;
    private final MemberMapper memberMapper;

    public SharingController(SharingService sharingService, SharingMapper sharingMapper, DreamService dreamService, StampService stampService, MemberMapper memberMapper) {
        this.sharingService = sharingService;
        this.sharingMapper = sharingMapper;
        this.dreamService = dreamService;
        this.stampService = stampService;
        this.memberMapper = memberMapper;
    }

    @PostMapping
    public ResponseEntity postSharing(@PathVariable("dreamId") Long dreamId,
                                      Authentication authentication) {

//        Sharing newSharing = new Sharing();
        if (authentication == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SharingDto.Post requestBody = new SharingDto.Post(dreamId);
        requestBody.setDreamId(dreamId);
        Sharing sharing = sharingMapper.sharingPostToSharing(requestBody);

        Sharing createSharing = sharingService.logSharing(sharing, authentication.getName());

        if(createSharing.getMember().getStamp().getCount() % 5 == 0 && createSharing.getMember().getStamp().getCount() != 0){
            int size = createSharing.getMember().getMemberRewardPictures().size();
            MemberRewardPictureDto.Response response = memberMapper.memberRewardPictureToMemberRewardPictureDto(createSharing.getMember().getMemberRewardPictures().get(size-1));
            return new ResponseEntity(response,HttpStatus.OK);
        }


        return new ResponseEntity<>(HttpStatus.OK);
    }

}