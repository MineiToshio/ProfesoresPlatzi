package com.toshiominei.profesoresplatzi.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.toshiominei.profesoresplatzi.dao.TeacherDao;
import com.toshiominei.profesoresplatzi.model.Teacher;

@Service("teacherService")
@Transactional
public class TeacherServiceImpl implements TeacherService {

	@Autowired
	TeacherDao _teacherDao;
	
	@Override
	public void saveTeacher(Teacher teacher) {
		_teacherDao.saveTeacher(teacher);
	}

	@Override
	public List<Teacher> findAllTeachers() {
		return _teacherDao.findAllTeachers();
	}

	@Override
	public void deleteTeacherById(Long id) {
		_teacherDao.deleteTeacherById(id);
	}

	@Override
	public void updateTeacher(Teacher teacher) {
		_teacherDao.updateTeacher(teacher);
	}

	@Override
	public Teacher findById(Long idTeacher) {
		return _teacherDao.findById(idTeacher);
	}

	@Override
	public Teacher findByName(String name) {
		return _teacherDao.findByName(name);
	}

}
