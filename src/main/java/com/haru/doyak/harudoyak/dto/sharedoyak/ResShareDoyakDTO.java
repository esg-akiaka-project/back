package com.haru.doyak.harudoyak.dto.sharedoyak;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResShareDoyakDTO {

    private String shareAuthorNickname;
    private Long shareDoyakId;
    private String goalName;
    private String shareContent;
    private String shareImageUrl;
    private Long commentCount;
    private Long doyakCount;

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
