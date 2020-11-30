package com.dtonetcdf.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dtonetcdf.entity.MyVariableDTO;
import com.dtonetcdf.entity.MyVariableDTOList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class UploadController {

    private final String UPLOAD_DIR = "./uploads/";

    @GetMapping("/")
    public String homepage(Model model) {
    	System.out.println("pase por el index");
        return "index";
    }
    
    @GetMapping("/detalle/{name}")
    public String detalle(@PathVariable("name") String name,  Model model) {
    	System.out.println("pase por el detalle "+name);
    	RestTemplate restTemplate = new RestTemplate();
    	String fooResourceUrl
    	  = "http://localhost:8080/getvariable?name="+name;
    	ResponseEntity<String> response
    	  = restTemplate.getForEntity(fooResourceUrl , String.class);
    	model.addAttribute("detallestring",response.getBody() );
    	System.out.println("detalle "+response.getBody() );
    	
        return "detalle";
    }
    
    
    
    @GetMapping("/listar")
    public String listar(Model model) throws JsonMappingException, JsonProcessingException {
    	RestTemplate restTemplate = new RestTemplate();
    	String fooResourceUrl
    	  = "http://localhost:8080/getvariables";
    	ResponseEntity<MyVariableDTO[]> response
    	  = restTemplate.getForEntity(fooResourceUrl, MyVariableDTO[].class);
    	
    	List<MyVariableDTO> my = Arrays.asList(response.getBody());
    	model.addAttribute("MyVariableDTOList", my);
    	System.out.println("pase por aca "+my.toString());
        return "listar";
    }
    
   

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) throws IOException {

        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }

        // normalize the file path
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // save the file on the local file system
        
        	
        	HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // This nested HttpEntiy is important to create the correct
            // Content-Disposition entry with metadata "name" and "filename"
            MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
            ContentDisposition contentDisposition = ContentDisposition
                    .builder("form-data")
                    .name("file")
                    .filename(fileName)
                    .build();
            fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
            HttpEntity<byte[]> fileEntity = new HttpEntity<>(file.getBytes(), fileMap);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileEntity);
            body.add("name", "outN.1");

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                		"http://localhost:8080/upload",
                        HttpMethod.POST,
                        requestEntity,
                        String.class);
                
                System.out.println("response "+response);
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
            }
        attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');

        return "redirect:/";
        
    }
    
    
  

}
