package com.toshiominei.profesoresplatzi.dao;

import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.toshiominei.profesoresplatzi.model.Teacher;
import com.toshiominei.profesoresplatzi.model.TeacherSocialMedia;

@Repository
@Transactional
public class TeacherDaoImpl extends AbstractSession implements TeacherDao {

	@Override
	public void saveTeacher(Teacher teacher) {
		getSession().persist(teacher);
	}

	@Override
	public List<Teacher> findAllTeachers() {
		return getSession().createQuery("from Teacher").list();
	}

	@Override
	public void deleteTeacherById(Long idTeacher) {
		Teacher teacher = findById(idTeacher);
				
		if(teacher != null) {
			
			Iterator<TeacherSocialMedia> i = teacher.getTeacherSocialMedias().iterator();
			
			while(i.hasNext()) {
				TeacherSocialMedia teacherSocialMedia = i.next();
				i.remove();
				getSession().delete(teacherSocialMedia);
			}
			teacher.getTeacherSocialMedias().clear();
			getSession().delete(teacher);
		}
	}

	@Override
	public void updateTeacher(Teacher teacher) {
		getSession().update(teacher);
	}

	@Override
	public Teacher findById(Long idTeacher) {
		return (Teacher)getSession().get(Teacher.class, idTeacher);
	}

	@Override
	public Teacher findByName(String name) {
		return (Teacher)getSession()
				.createQuery("from Teacher where name = :name")
				.setParameter("name", name).uniqueResult();
	}

}
