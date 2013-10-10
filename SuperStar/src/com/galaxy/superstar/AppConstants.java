package com.galaxy.superstar;

public interface AppConstants {
	String version = "app_version";
	
	String current_version = "1.0";
	
	String service_url = "http://www.ycpai.com/";
	
	int order_id_length = 11;
	int order_id_verify_mod = 7;
	
	float image_round = 12.0f;
	String default_preference = "default_sp";
	
	String user_id = "user_id";
	String login_str = "login_str";
	String active_num = "active_num";
	
	String air_company_id = "air_company_id";
	
	String ikey_buddy_src = "intent_key_buddy_src";
	String ikey_buddy_id = "buddy_id";
	String ikey_buddy_head = "buddy_head";
	String ikey_src = "intent_key_src";
	
	String push_intent_action = "com.avos.ycpai";
	
	String TENCENT_APP_ID = "100471128";
	String QQ_SCOPE = "get_user_info,get_simple_userinfo,get_user_profile,get_app_friends,add_one_blog"
            + "add_share,add_topic,list_album,upload_pic,add_album,set_user_face,get_vip_info,get_vip_rich_info,get_intimate_friends_weibo,match_nick_tips_weibo";

	String weiboApiKey = "3675955695";
	String weiboRedirectURL = "http://www.ycpai.com";
	String SINA_SCOPE = "email,direct_messages_read,direct_messages_write," +
			"friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
				"follow_app_official_microblog";

	int notification_id = 0x1234;
	
	String need_relogin = "已在其它设备登陆，请重新登陆";
	String login_source = "relogin";
}
