package com.infy.infyinterns.api;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.service.ProjectAllocationService;

@RestController
@RequestMapping("/infyinterns")
@Validated
public class ProjectAllocationAPI{
	
	@Autowired
	ProjectAllocationService projectAllocationService;
	
	@Autowired
	Environment environment;

    // add new project along with mentor details
	@PostMapping("/project")
    public ResponseEntity<String> allocateProject(@Valid @RequestBody ProjectDTO projectDTO) throws InfyInternException{
		Integer projectId = projectAllocationService.allocateProject(projectDTO);
		String message = environment.getProperty("API.ALLOCATION_SUCCESS") + " : " + projectId;
    	return new ResponseEntity<String>(message, HttpStatus.CREATED);
    }

    // get mentors based on idea owner
	@GetMapping("/mentor/{numberOfProjectsMentored}")
    public ResponseEntity<List<MentorDTO>> getMentors(@PathVariable Integer numberOfProjectsMentored) throws InfyInternException{
		List<MentorDTO> mentorsList = projectAllocationService.getMentors(numberOfProjectsMentored);
    	return new ResponseEntity<>(mentorsList, HttpStatus.OK);
    }

    // update the mentor of a project
	@PutMapping("/project/{projectId}/{mentorId}")
    public ResponseEntity<String> updateProjectMentor(@PathVariable Integer projectId, 
    		                                          @PathVariable @Min(value = 1000, message = "{mentor.mentorid.invalid}") @Max(value = 9999, message = "{mentor.mentorid.invalid}") Integer mentorId) throws InfyInternException{
		projectAllocationService.updateProjectMentor(projectId, mentorId);
		String message = environment.getProperty("API.PROJECT_UPDATE_SUCCESS");
    	return new ResponseEntity<String>(message, HttpStatus.OK);
    }

    // delete a project
	@DeleteMapping("/project/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Integer projectId) throws InfyInternException{
    	projectAllocationService.deleteProject(projectId);
    	String message = environment.getProperty("API.PROJECT_DELETE _SUCCESS");
    	return new ResponseEntity<String>(message, HttpStatus.OK);
    }

}
