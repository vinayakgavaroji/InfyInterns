package com.infy.infyinterns.service;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.entity.Mentor;
import com.infy.infyinterns.entity.Project;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.repository.MentorRepository;
import com.infy.infyinterns.repository.ProjectRepository;

@Service(value = "projectAllocationService")
@Transactional
public class ProjectAllocationServiceImpl implements ProjectAllocationService {
	
	@Autowired
	MentorRepository mentorRepository;
	
	@Autowired
	ProjectRepository projectRepository;

	@Override
	public Integer allocateProject(ProjectDTO projectDTO) throws InfyInternException {
		
        Mentor mentor = mentorRepository.findById(projectDTO.getMentorDTO().getMentorId()).orElseThrow(() -> new InfyInternException("Service.MENTOR_NOT_FOUND"));
		
        if (mentor.getNumberOfProjectsMentored() >= 3) {
            throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
        }
        
        Project project = new Project();
        project.setProjectName(projectDTO.getProjectName());
        project.setIdeaOwner(projectDTO.getIdeaOwner());
        project.setReleaseDate(projectDTO.getReleaseDate());
        project.setMentor(mentor);
        
        projectRepository.save(project);
        mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored() + 1);
        mentorRepository.save(mentor);
        
		return project.getProjectId();
	}

	
	@Override
	public List<MentorDTO> getMentors(Integer numberOfProjectsMentored) throws InfyInternException {
		
		List<MentorDTO> mentorDTOList = new ArrayList<MentorDTO>();
		
		List<Mentor> mentors = mentorRepository.findByNumberOfProjectsMentored(numberOfProjectsMentored);
		
        if (mentors.isEmpty()) {
            throw new InfyInternException("Service.MENTOR_NOT_FOUND");
        }
        
        for(Mentor mentor : mentors) {
        	MentorDTO mentorDTO = new MentorDTO();
        	mentorDTO.setMentorId(mentor.getMentorId());
        	mentorDTO.setMentorName(mentor.getMentorName());
        	mentorDTO.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored());
        	
        	mentorDTOList.add(mentorDTO);
        }
		
		return mentorDTOList;
	}


	@Override
	public void updateProjectMentor(Integer projectId, Integer mentorId) throws InfyInternException {
		
		Mentor mentor = mentorRepository.findById(mentorId).orElseThrow(() -> new InfyInternException("Service.MENTOR_NOT_FOUND"));
        
        if (mentor.getNumberOfProjectsMentored() >= 3) {
            throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
        }

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new InfyInternException("Service.PROJECT_NOT_FOUND"));
        project.setMentor(mentor);
        
        mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored() + 1);

        projectRepository.save(project);
        mentorRepository.save(mentor);
	}

	@Override
	public void deleteProject(Integer projectId) throws InfyInternException {
		
		Project project = projectRepository.findById(projectId).orElseThrow(() -> new InfyInternException("Service.PROJECT_NOT_FOUND"));

        if (project.getMentor() != null) {
            Mentor mentor = project.getMentor();
            project.setMentor(null);

            mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored() - 1);
            mentorRepository.save(mentor);
        }
        projectRepository.delete(project);
	}
}