package com.toshiominei.profesoresplatzi.dao;

import java.util.List;

import com.toshiominei.profesoresplatzi.model.SocialMedia;
import com.toshiominei.profesoresplatzi.model.TeacherSocialMedia;

public interface SocialMediaDao {
	void saveSocialMedia(SocialMedia socialMedia);
	void deleteSocialMediaById(Long idSocialMedia);
	void updateSocialMedia(SocialMedia socialMedia);
	List<SocialMedia> findAllSocialMedias();
	SocialMedia findById(Long idSocialMedia);
	SocialMedia findByName(String name);
	TeacherSocialMedia findSocialMediaByIdAndName(Long idSocialMedia, String nickname);
	TeacherSocialMedia findSocialMediaByIdTeacherAndIdSocialMedia(Long idTeacher, Long idSocialMedia);
}
