package com.haru.doyak.harudoyak.dto.sharedoyak;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResShareDoyakDTO {

    private Long shareDoyakId;          // 서로도약pk
    private String shareContent;        // 서로도약 내용
    private String shareImageUrl;       // 서로도약 이미지파일 url

    @Builder
    private ResShareDoyakDTO(Long shareDoyakId, String shareContent, String shareImageUrl) {
        this.shareDoyakId = shareDoyakId;
        this.shareContent = shareContent;
        this.shareImageUrl = shareImageUrl;
    }

    /*
     * 서로도약 목록
     * */
    @Getter
    @Setter
    public static class ResShareDoyakDTOS {
        private String shareAuthorNickname; // 서로도약 작성자 닉네임
        private Long shareDoyakId;          // 서로도약pk
        private String goalName;            // 서로도약 작성자 목표명
        private String shareContent;        // 서로도약 내용
        private String shareImageUrl;       // 서로도약 이미지파일 url
        private Long commentCount;          // 댓글 총 수
        private Long doyakCount;            // 도약 총 수
    }

    /*
    * 회원의 서로도약 목록
    * */
    @Getter
    @Setter
    public static class ResMemberShareDoyakDYO {
        private String shareAuthorNickname;
        private Long shareDoyakId;
    }

}
