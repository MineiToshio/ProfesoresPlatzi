package com.toshiominei.profesoresplatzi.service;

import java.util.List;

import com.toshiominei.profesoresplatzi.model.Teacher;

public interface TeacherService {
	void saveTeacher(Teacher teacher);
	List<Teacher> findAllTeachers();
	void deleteTeacherById(Long id);
	void updateTeacher(Teacher teacher);
	Teacher findById(Long idTeacher);
	Teacher findByName(String name);
}
