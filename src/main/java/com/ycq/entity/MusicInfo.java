package com.ycq.entity;

import lombok.Data;

import java.util.Date;

/**
 * Description:
 *
 * @author 杨存秋
 * @version 1.0
 * date: 2020/1/19 13:56
 * @since JDK 1.8
 */
@Data
public class MusicInfo {

    private String uid;
    private String shareUid;
    private String title;
    private int time;
    private Date date;

}
