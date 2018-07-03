package com.toshiominei.profesoresplatzi.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.toshiominei.profesoresplatzi.dao.SocialMediaDao;
import com.toshiominei.profesoresplatzi.model.SocialMedia;
import com.toshiominei.profesoresplatzi.model.TeacherSocialMedia;

@Service("socialMediaService")
@Transactional
public class SocialMediaServiceImpl implements SocialMediaService {

	@Autowired
	SocialMediaDao _socialMediaDao;
	
	@Override
	public void saveSocialMedia(SocialMedia socialMedia) {
		_socialMediaDao.saveSocialMedia(socialMedia);
	}

	@Override
	public void deleteSocialMediaById(Long idSocialMedia) {
		_socialMediaDao.deleteSocialMediaById(idSocialMedia);
	}

	@Override
	public void updateSocialMedia(SocialMedia socialMedia) {
		_socialMediaDao.updateSocialMedia(socialMedia);
	}

	@Override
	public List<SocialMedia> findAllSocialMedias() {
		return _socialMediaDao.findAllSocialMedias();
	}

	@Override
	public SocialMedia findById(Long idSocialMedia) {
		return _socialMediaDao.findById(idSocialMedia);
	}

	@Override
	public SocialMedia findByName(String name) {
		return _socialMediaDao.findByName(name);
	}

	@Override
	public TeacherSocialMedia findSocialMediaByIdAndName(Long idSocialMedia, String nickname) {
		return _socialMediaDao.findSocialMediaByIdAndName(idSocialMedia, nickname);
	}

	@Override
	public TeacherSocialMedia findSocialMediaByIdTeacherAndIdSocialMedia(Long idTeacher, Long idSocialMedia) {
		return _socialMediaDao.findSocialMediaByIdTeacherAndIdSocialMedia(idTeacher, idSocialMedia);
	}
}
