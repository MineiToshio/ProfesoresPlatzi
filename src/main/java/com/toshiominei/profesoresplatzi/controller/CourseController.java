package com.toshiominei.profesoresplatzi.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.toshiominei.profesoresplatzi.model.Course;
import com.toshiominei.profesoresplatzi.service.CourseService;
import com.toshiominei.profesoresplatzi.util.CustomErrorType;

@Controller
@RequestMapping("/v1")
public class CourseController {

	@Autowired
	CourseService _courseService;
	
	//GET
		@RequestMapping(value="/courses", method = RequestMethod.GET, headers = "Accept=application/json")
		public ResponseEntity<List<Course>> getCourses(@RequestParam(value="name", required=false) String name, @RequestParam(value="id_teacher", required=false) Long id_teacher) {
			
			List<Course> courses = new ArrayList<>();
			
			if(id_teacher != null) {
				
				courses = _courseService.findByIdTeacher(id_teacher);
				
				if(courses.isEmpty())
					return new ResponseEntity(HttpStatus.NO_CONTENT);
				
			} else if(name != null) {
				
				Course Course = _courseService.findByName(name);
				
				if(Course == null)
					return new ResponseEntity(HttpStatus.NOT_FOUND);
				
				courses.add(Course);
				
			} else {
				courses = _courseService.findAllCourses();
				
				if(courses.isEmpty())
					return new ResponseEntity(HttpStatus.NO_CONTENT);
				
			}
			
			return new ResponseEntity<List<Course>>(courses, HttpStatus.OK);
		}
		
		//GET
		@RequestMapping(value="/Courses/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
		public ResponseEntity<Course> getCourseById(@PathVariable("id") Long idCourse) { 
			if(idCourse == null || idCourse <= 0)
				return new ResponseEntity(new CustomErrorType("IdCourse is required"), HttpStatus.CONFLICT);
			
			Course course = _courseService.findById(idCourse);
			
			if(course == null)
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			
			return new ResponseEntity<Course>(course, HttpStatus.OK);
		}
		
		//POST
		@RequestMapping(value="/Courses", method = RequestMethod.POST, headers = "Accept=application/json")
		public ResponseEntity<?> createCourse(@RequestBody Course course, UriComponentsBuilder uriComponentsBuilder) {
			if(course.getName().equals(null) || course.getName().isEmpty())
				return new ResponseEntity(new CustomErrorType("CourseName is required"), HttpStatus.CONFLICT);
			
			if(_courseService.findByName(course.getName()) != null)
				return new ResponseEntity(new CustomErrorType("Unable to create. A course with name " + course.getName() + " already exist."),HttpStatus.CONFLICT);
			
			_courseService.saveCourse(course);
			Course Course2 = _courseService.findByName(course.getName());
			
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(uriComponentsBuilder.path("v1/Courses/{id}")
					.buildAndExpand(Course2.getIdCourse())
					.toUri());
			
			return new ResponseEntity<String>(headers, HttpStatus.CREATED);
		}
		
		//UPDATE
		@RequestMapping(value="/Courses/{id}", method = RequestMethod.PATCH, headers = "Accept=application/json")
		public ResponseEntity<?> updateCourse(@PathVariable("id") Long idCourse, @RequestBody Course course) {
			
			if(idCourse == null || idCourse <= 0)
				return new ResponseEntity(new CustomErrorType("IdCourse is required"), HttpStatus.CONFLICT);
			
			Course currentCourse = _courseService.findById(idCourse);
			
			if(currentCourse == null)
				return new ResponseEntity(HttpStatus.NO_CONTENT); 
			
			currentCourse.setName(course.getName());
			currentCourse.setThemes(course.getThemes());
			currentCourse.setProject(course.getProject());
			
			_courseService.updateCourse(currentCourse);
			return new ResponseEntity<Course>(currentCourse, HttpStatus.OK); 
		}
		
		//DELETE
		@RequestMapping(value="/Courses/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
		public ResponseEntity<?> deleteCourse(@PathVariable("id") Long idCourse) {
			
			if(idCourse == null || idCourse <= 0)
				return new ResponseEntity(new CustomErrorType("IdCourse is required"), HttpStatus.CONFLICT);
			
			Course Course = _courseService.findById(idCourse);
			
			if(Course == null)
				return new ResponseEntity(HttpStatus.NOT_FOUND);
			
			_courseService.deleteCourseById(idCourse);
			return new ResponseEntity<Course>(HttpStatus.OK);
		}
}
