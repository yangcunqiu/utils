package com.ycq.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ycq.entity.KSongUserInfo;
import com.ycq.entity.MusicInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 解析全民k歌
 *
 * @author 杨存秋
 * @version 1.0
 * date: 2020/1/19 13:40
 * @since JDK 1.8
 */
public class KSongUtil {

    private static RestTemplate restTemplate = new RestTemplate();

    static {
        // 编码
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }



    public static KSongUserInfo getUserInfo(){
        final String shareUid = "66959a8c262a348b";
        return getUserInfo(shareUid);
    }

    // 根据shareUid获取用户基本信息
    public static KSongUserInfo getUserInfo(String shareUid){
        String getUserInfoUrl = "http://node.kg.qq.com/cgi/fcgi-bin/kg_ugc_get_homepage?type=get_uinfo&start=1&num=15&share_uid=";
        getUserInfoUrl += shareUid;
        JSONObject userInfoMap = getData(getUserInfoUrl);
        if (userInfoMap == null || userInfoMap.size() == 0){
            return null;
        }
        String username = (String) userInfoMap.get("nickname");
        int gender = (int) userInfoMap.get("gender");
        int age = (int) userInfoMap.get("age");
        String headImgPath = (String) userInfoMap.get("head_img_url");
        int musicCount = (int) userInfoMap.get("ugc_total_count");
        int fansCount = (int) userInfoMap.get("follower");
        int followCount = (int) userInfoMap.get("following");
        List<MusicInfo> musicInfoList = getMusicInfo(shareUid, musicCount);
        KSongUserInfo userInfo = new KSongUserInfo();
        userInfo.setShareUid(shareUid);
        userInfo.setUsername(username);
        userInfo.setAge(age);
        userInfo.setGender(gender == 1 ? "男" : "女");
        userInfo.setHeadImgPath(headImgPath);
        userInfo.setFansCount(fansCount);
        userInfo.setFollowCount(followCount);
        userInfo.setMusicCount(musicCount);
        userInfo.setMusicList(musicInfoList);
        return userInfo;
    }

    public static String downloadMusic(String shareUid) throws IOException {
        return downloadMusic(shareUid, "d://download/music/");
    }

    // 下载
    public static String downloadMusic(String shareUid, String path) throws IOException {
        long start = new Date().getTime();
        if (StringUtils.isEmpty(path)){
            path = "d://download/music/";
        }
        File file = new File(path);
        if (!file.exists()){
            boolean mkdirs = file.mkdirs();
            if (!mkdirs){
                return path + "路径创建不成功";
            }
        }
        KSongUserInfo userInfo = getUserInfo(shareUid);
        if (userInfo == null){
            return "获取用户信息失败";
        }
        String username = userInfo.getUsername();
        List<MusicInfo> musicList = userInfo.getMusicList();
        if (musicList == null || musicList.size() == 0){
            return "该用户无歌曲";
        }
        int num = 1;
        for (MusicInfo musicInfo : musicList) {
            download(musicInfo, username, num++, path);
        }
        long end = new Date().getTime();
        Date time = new Date(end - start);
        return "歌曲下载完成, 路径: d://download/music/" + ", 总耗时: " + time.getTime()*1.0/1000 + "s";
    }

    public static String downloadMusic() throws IOException {
        final String shareUid = "66959a8c262a348b";
        final String path = "d://download/music/";
        return downloadMusic(shareUid, path);
    }

    private static void download(MusicInfo musicInfo, String username, int num, String basePath) throws IOException {
        String downloadUrl = "http://node.kg.qq.com/cgi/fcgi-bin/fcg_get_play_url?shareid=";
        downloadUrl += musicInfo.getShareUid();
        URL url = new URL(downloadUrl);
        InputStream inputStream = url.openStream();
        String subPath = username + "/" ;
        String path = basePath + subPath;
        File dir = new File(path);
        if (!dir.exists()){
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs){
                throw new IOException(path + "文件夹创建失败");
            }
        }
        String fileName = num + ". " + musicInfo.getTitle() + ".mp3";
        File file = new File(path+fileName);
        if (file.exists()){
            return;
        }
        OutputStream outputStream = new FileOutputStream(file);
        IOUtils.copy(inputStream, outputStream);
    }

    private static List<MusicInfo> getMusicInfo(String shareUid, int musicCount){
        List<MusicInfo> musicList = new ArrayList<>();
        int num = (int) Math.ceil(musicCount * 1.0 / 15);
        for (int i = 1; i <= num; i++) {

            String getUserInfoUrl = "http://node.kg.qq.com/cgi/fcgi-bin/kg_ugc_get_homepage?type=get_uinfo&start="+i+"&num=15&share_uid=" + shareUid;
            JSONObject userInfoMap = getData(getUserInfoUrl);
            if (userInfoMap == null || userInfoMap.size() == 0){
                return null;
            }
            JSONArray ugclist = userInfoMap.getJSONArray("ugclist");
            for (Object music : ugclist) {
                MusicInfo musicInfo = new MusicInfo();
                JSONObject m = (JSONObject) music;
                String s = m.toJSONString();
                Map map = JSON.parseObject(s, Map.class);
                musicInfo.setUid(shareUid);
                musicInfo.setTitle((String) map.get("title"));
                musicInfo.setShareUid((String) map.get("shareid"));
                musicInfo.setTime((Integer) map.get("time"));
                musicList.add(musicInfo);
            }
        }
        // 按发布时间排序
        musicList.sort(((o1, o2) -> o2.getTime() - o1.getTime()));
        return musicList;
    }

    // 发送请求
    private static JSONObject getData(String getUserInfoUrl) {
        String userInfoStr = restTemplate.getForObject(getUserInfoUrl, String.class);
        if (userInfoStr == null || StringUtils.isEmpty(userInfoStr)){
            return null;
        }
        String substring = userInfoStr.substring(18);
        String substring1 = substring.substring(0, substring.length() - 1);
        Map dataMap = JSON.parseObject(substring1, Map.class);
        return (JSONObject) dataMap.get("data");
    }

}
