package com.ycq.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ycq.entity.KSongUserInfo;
import org.junit.Test;

import java.io.IOException;

/**
 * Description:
 *
 * @author 杨存秋
 * @version 1.0
 * date: 2020/1/19 15:36
 * @since JDK 1.8
 */
public class KSongUtilTest {


    // 根据shareUid获取用户基本信息
    @Test
    public void userInfo(){
        // 全民k歌用户shareUid
        String shareUid = "66959a8c262a348b";
        KSongUserInfo userInfo = KSongUtil.getUserInfo(shareUid);
        // 格式化json结果
        String objJSON = JSON.toJSONString(userInfo, SerializerFeature.PrettyFormat,
                SerializerFeature.WriteDateUseDateFormat);
        System.out.println(objJSON);
    }

    // 下载用户所有歌曲
    // 默认保存路径为 d://download/music/
    @Test
    public void download() throws IOException {
        String shareUid = "66959a8c262a348b";
        String path = "d://download/test/";
        String downInfo = KSongUtil.downloadMusic(shareUid, path);
        System.out.println(downInfo);
    }

}
