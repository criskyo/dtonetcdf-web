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

	private static final String URL_UPLOAD = "http://localhost:8080/upload";
	private static final String URL_GET_VARIABLE = "http://localhost:8080/getvariable?name=";
    private static final String URL_GET_VARIABLES = "http://localhost:8080/getvariables";
	private final String UPLOAD_DIR = "./uploads/";
    

    @GetMapping("/")
    public String homepage(Model model) {
        return "index";
    }
    
    @GetMapping("/detalle/{name}")
    public String detalle(@PathVariable("name") String name,  Model model) {
    	RestTemplate restTemplate = new RestTemplate();
		String fooResourceUrl
    	  = URL_GET_VARIABLE+name;
    	ResponseEntity<String> response
    	  = restTemplate.getForEntity(fooResourceUrl , String.class);
    	model.addAttribute("detallestring",response.getBody() );
        return "detalle";
    }
    
    
    
    @GetMapping("/listar")
    public String listar(Model model) throws JsonMappingException, JsonProcessingException {
    	RestTemplate restTemplate = new RestTemplate();
    	String fooResourceUrl
    	  = URL_GET_VARIABLES;
    	ResponseEntity<MyVariableDTO[]> response
    	  = restTemplate.getForEntity(fooResourceUrl, MyVariableDTO[].class);
    	
    	List<MyVariableDTO> my = Arrays.asList(response.getBody());
    	model.addAttribute("MyVariableDTOList", my);
    	System.out.println("pase por aca "+my.toString());
        return "listar";
    }
    
   

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) throws IOException {
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Porfavor seleccionar un archivo a subir.");
            return "redirect:/";
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        	HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
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
            body.add("name", fileName);

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                		URL_UPLOAD,
                        HttpMethod.POST,
                        requestEntity,
                        String.class);
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
            }
        attributes.addFlashAttribute("message", "El archivo " + fileName + " se subio correctamente!");
        return "redirect:/";
    }
    
    
  

}
