package com.toshiominei.profesoresplatzi.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.toshiominei.profesoresplatzi.model.SocialMedia;
import com.toshiominei.profesoresplatzi.model.Teacher;
import com.toshiominei.profesoresplatzi.model.TeacherSocialMedia;
import com.toshiominei.profesoresplatzi.service.SocialMediaService;
import com.toshiominei.profesoresplatzi.service.TeacherService;
import com.toshiominei.profesoresplatzi.util.CustomErrorType;

@Controller
@RequestMapping("/v1")
public class TeacherController {

	@Autowired
	private TeacherService _teacherService;
	
	@Autowired
	private SocialMediaService _socialMediaService;
	
	//GET
	@RequestMapping(value="/teachers", method = RequestMethod.GET, headers = "Accept=application/json")
	public ResponseEntity<List<Teacher>> getTeachers(@RequestParam(value="name", required=false) String name) {
		
		List<Teacher> socialMedias = new ArrayList<>();
		
		if(name == null) {
			socialMedias = _teacherService.findAllTeachers();
			
			if(socialMedias.isEmpty())
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			
		} else {
			Teacher socialMedia = _teacherService.findByName(name);
					
			if(socialMedia == null)
				return new ResponseEntity(HttpStatus.NOT_FOUND);
			
			socialMedias.add(socialMedia);
		}
		
		return new ResponseEntity<List<Teacher>>(socialMedias, HttpStatus.OK);
	}
	
	//GET
	@RequestMapping(value="/teachers/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	public ResponseEntity<Teacher> getTeacherById(@PathVariable("id") Long idTeacher) { 
		if(idTeacher == null || idTeacher <= 0)
			return new ResponseEntity(new CustomErrorType("IdTeacher is required"), HttpStatus.CONFLICT);
		
		Teacher socialMedia = _teacherService.findById(idTeacher);
		
		if(socialMedia == null)
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		
		return new ResponseEntity<Teacher>(socialMedia, HttpStatus.OK);
	}
	
	//POST
	@RequestMapping(value="/teachers", method = RequestMethod.POST, headers = "Accept=application/json")
	public ResponseEntity<?> createTeacher(@RequestBody Teacher socialMedia, UriComponentsBuilder uriComponentsBuilder) {
		if(socialMedia.getName().equals(null) || socialMedia.getName().isEmpty())
			return new ResponseEntity(new CustomErrorType("socialMediaName is required"), HttpStatus.CONFLICT);
		
		if(_teacherService.findByName(socialMedia.getName()) != null)
			return new ResponseEntity(HttpStatus.NO_CONTENT); 
		
		_teacherService.saveTeacher(socialMedia);
		Teacher socialMedia2 = _teacherService.findByName(socialMedia.getName());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uriComponentsBuilder.path("v1/socialMedias/{id}")
				.buildAndExpand(socialMedia2.getIdTeacher())
				.toUri());
		
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
	
	//UPDATE
	@RequestMapping(value="/teachers/{id}", method = RequestMethod.PATCH, headers = "Accept=application/json")
	public ResponseEntity<?> updateTeacher(@PathVariable("id") Long idTeacher, @RequestBody Teacher socialMedia) {
		
		if(idTeacher == null || idTeacher <= 0)
			return new ResponseEntity(new CustomErrorType("IdTeacher is required"), HttpStatus.CONFLICT);
		
		Teacher currentTeacher = _teacherService.findById(idTeacher);
		
		if(currentTeacher == null)
			return new ResponseEntity(HttpStatus.NO_CONTENT); 
		
		currentTeacher.setName(socialMedia.getName());
		currentTeacher.setAvatar(socialMedia.getAvatar());
		currentTeacher.setIdTeacher(socialMedia.getIdTeacher());
		
		_teacherService.updateTeacher(currentTeacher);
		return new ResponseEntity<Teacher>(currentTeacher, HttpStatus.OK); 
	}
	
	//DELETE
	@RequestMapping(value="/teachers/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseEntity<?> deleteTeacher(@PathVariable("id") Long idTeacher) {
		
		if(idTeacher == null || idTeacher <= 0)
			return new ResponseEntity(new CustomErrorType("IdTeacher is required"), HttpStatus.CONFLICT);
		
		Teacher socialMedia = _teacherService.findById(idTeacher);
		
		if(socialMedia == null)
			return new ResponseEntity(new CustomErrorType("Unable to delete. teacher with id " + idTeacher + " not found."), HttpStatus.NOT_FOUND);
		
		_teacherService.deleteTeacherById(idTeacher);
		return new ResponseEntity<Teacher>(HttpStatus.OK);
	}
	
	public static final String TEACHER_UPLOADED_FOLDER = "images/teachers/";
	//CREATE TEACHER IMAGE
	@RequestMapping(value="teachers/images", method = RequestMethod.POST, headers = "content-type=multipart/form-data")
	public ResponseEntity<byte[]> uploadTeacherImage(@RequestParam("id_teacher") Long idTeacher, @RequestParam("file") MultipartFile multipartFile, UriComponentsBuilder uriComponentsBuilder) {
		
		if(idTeacher == null)
			return new ResponseEntity(new CustomErrorType("Please set id_teacher."), HttpStatus.NO_CONTENT);
		
		if(multipartFile.isEmpty())
			return new ResponseEntity(new CustomErrorType("Please select a file to upload."), HttpStatus.NO_CONTENT);
		
		Teacher teacher = _teacherService.findById(idTeacher);
		
		if(teacher == null)
			return new ResponseEntity(new CustomErrorType("Unable to delete. teacher with id " + idTeacher + " not found."), HttpStatus.NOT_FOUND);
		
		if(!teacher.getAvatar().isEmpty() || teacher.getAvatar() != null) {
			String fileName = teacher.getAvatar();
			Path path = Paths.get(fileName);
			File f = path.toFile();
			if(f.exists())
				f.delete();
		}
		
		try {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String dateName = dateFormat.format(date);
			
			String fileName = String.valueOf(idTeacher) + "-pictureTeacher-" + dateName + "." + multipartFile.getContentType().split("/")[1];
			teacher.setAvatar(TEACHER_UPLOADED_FOLDER + fileName);
			
			byte[] bytes = multipartFile.getBytes();
			Path path = Paths.get(TEACHER_UPLOADED_FOLDER + fileName);
			Files.write(path, bytes);
			
			_teacherService.updateTeacher(teacher);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(new CustomErrorType("Error during upload: " + multipartFile.getOriginalFilename() + "."), HttpStatus.CONFLICT);
		}
	}
	
	//GET IMAGES
	@RequestMapping(value="/teachers/{id_teacher}/images", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getTeacherImage(@PathVariable("id_teacher") Long idTeacher) {
		
		if(idTeacher == null)
			return new ResponseEntity(new CustomErrorType("Id_teacher is required"), HttpStatus.NO_CONTENT);
		
		Teacher teacher = _teacherService.findById(idTeacher);
		
		if(teacher == null)
			return new ResponseEntity(new CustomErrorType("Teacher with id_teacher: " + idTeacher + " not found."), HttpStatus.NOT_FOUND);
		
		try {
			
			String fileName = teacher.getAvatar();
			Path path = Paths.get(fileName);
			File f = path.toFile();
			if(!f.exists())
				return new ResponseEntity(new CustomErrorType("Image not found."), HttpStatus.NOT_FOUND);
			
			byte[] image = Files.readAllBytes(path);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(new CustomErrorType("Error showing image."), HttpStatus.CONFLICT);
		}
	}
	
	@RequestMapping(value="/teachers/{id_teacher}/images", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseEntity<?> deleteTeacherImage(@PathVariable("id_teacher") Long idTeacher) {
		
		if(idTeacher == null)
			return new ResponseEntity(new CustomErrorType("Id_teacher is required"), HttpStatus.NO_CONTENT);
		
		Teacher teacher = _teacherService.findById(idTeacher);
		
		if(teacher == null)
			return new ResponseEntity(new CustomErrorType("Teacher with id_teacher: " + idTeacher + " not found."), HttpStatus.NOT_FOUND);
		
		if(teacher.getAvatar().isEmpty() || teacher.getAvatar() == null)
			return new ResponseEntity(new CustomErrorType("This teacher doesn't have an image uploaded."), HttpStatus.NO_CONTENT);
		
		String fileName = teacher.getAvatar();
		Path path = Paths.get(fileName);
		File f = path.toFile();
		if(f.exists())
			f.delete();
		
		teacher.setAvatar("");
		_teacherService.updateTeacher(teacher);
		
		return new ResponseEntity<Teacher>(HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value="/teachers/socialMedias", method = RequestMethod.PATCH, headers = "Accept=application/json")
	public ResponseEntity<?> assignTeacherSocialMedia(@RequestBody Teacher teacher, UriComponentsBuilder uriComponentsBuilder) {
		
		if(teacher.getIdTeacher() == null)
			return new ResponseEntity(new CustomErrorType("We need id_teacher, id_social_media and nickname"), HttpStatus.NO_CONTENT);
		
		Teacher teacherSaved = _teacherService.findById(teacher.getIdTeacher());
		
		if(teacherSaved == null)
			return new ResponseEntity(new CustomErrorType("Teacher with id_teacher: " + teacher.getIdTeacher() + " not found."), HttpStatus.NOT_FOUND);
		
		if(teacher.getTeacherSocialMedias().size() == 0) {
			return new ResponseEntity(new CustomErrorType("We need id_teacher, id_social_media and nickname"), HttpStatus.NO_CONTENT);
		} else {
			Iterator<TeacherSocialMedia> i = teacher.getTeacherSocialMedias().iterator();
			
			while (i.hasNext()) {
				TeacherSocialMedia teacherSocialMedia = i.next();
				
				if(teacherSocialMedia.getSocialMedia() == null || teacherSocialMedia.getNickname() == null)
					return new ResponseEntity(new CustomErrorType("We need id_teacher, id_social_media and nickname"), HttpStatus.NO_CONTENT);
				else {
					TeacherSocialMedia tsmAux = _socialMediaService.findSocialMediaByIdAndName(teacherSocialMedia.getSocialMedia().getIdSocialMedia(), teacherSocialMedia.getNickname());
				
					//if(tsmAux != null)
					//	return new ResponseEntity(new CustomErrorType("The is social media " + teacherSocialMedia.getSocialMedia().getIdSocialMedia() + " with nickname: " + teacherSocialMedia.getNickname() + " already exists"), HttpStatus.NO_CONTENT);
						
					SocialMedia socialMedia = _socialMediaService.findById(teacherSocialMedia.getSocialMedia().getIdSocialMedia());
					
					if(socialMedia == null)
						return new ResponseEntity(new CustomErrorType("The is social media " + teacherSocialMedia.getSocialMedia().getIdSocialMedia() + " not found"), HttpStatus.NOT_FOUND);
					
					teacherSocialMedia.setSocialMedia(socialMedia);
					teacherSocialMedia.setTeacher(teacherSaved);
					
					if(tsmAux == null)
						teacherSaved.getTeacherSocialMedias().add(teacherSocialMedia);
					else {
						LinkedList<TeacherSocialMedia> teacherSocialMedias = new LinkedList<>();
						teacherSocialMedias.addAll(teacherSaved.getTeacherSocialMedias());
						
						for(int j = 0; j < teacherSocialMedias.size(); j++) {
							TeacherSocialMedia teacherSocialMedia2 = teacherSocialMedias.get(j);
							if(teacherSocialMedia.getTeacher().getIdTeacher() == teacherSocialMedia2.getTeacher().getIdTeacher() && teacherSocialMedia.getSocialMedia().getIdSocialMedia() == teacherSocialMedia2.getSocialMedia().getIdSocialMedia()) {
								teacherSocialMedia2.setNickname(teacherSocialMedia.getNickname());
								teacherSocialMedias.set(j, teacherSocialMedia2);
							}
							else 
								teacherSocialMedias.set(j, teacherSocialMedia2);
							
						}
						
						teacherSaved.getTeacherSocialMedias().clear();
						teacherSaved.getTeacherSocialMedias().addAll(teacherSocialMedias);
					}
				}
			}
		}
		
		_teacherService.updateTeacher(teacherSaved);
		return new ResponseEntity<Teacher>(teacherSaved, HttpStatus.OK);
	}
}
