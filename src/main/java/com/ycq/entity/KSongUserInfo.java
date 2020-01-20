package com.ycq.entity;

import lombok.Data;

import java.util.List;

/**
 * Description: 全民k歌 自定义 用户对象
 *
 * @author 杨存秋
 * @version 1.0
 * date: 2020/1/19 13:41
 * @since JDK 1.8
 */
@Data
public class KSongUserInfo {

    // shareUid
    private String shareUid;
    // 用户名
    private String username;
    // 性别
    private String gender;
    // 年龄
    private int age;
    // 头像地址
    private String headImgPath;
    // 作品数量
    private int musicCount;
    // 粉丝数
    private int fansCount;
    // 关注人数
    private int followCount;
    // 歌曲信息
    private List<MusicInfo> musicList;

}
