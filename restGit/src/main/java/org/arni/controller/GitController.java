package org.arni.controller;

import org.arni.model.ErrorMessage;
import org.arni.model.GitUser;
import org.arni.model.GitUserDTO;
import org.arni.service.CollectGitUserService;
import org.arni.service.ICalculationService;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("users")
public class GitController {

    private final CollectGitUserService collectGitUserService;
    private final ICalculationService calculationService;

    @Autowired
    public GitController(CollectGitUserService collectGitUserService, ICalculationService calculationService) {
        this.collectGitUserService = collectGitUserService;
        this.calculationService = calculationService;
    }

    @GetMapping(value = "/{userName}", produces = "application/json")
    ResponseEntity<?> getUserData(@PathVariable String userName) throws EncodingException, IOException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GitUser> response = null;
        try {
            response = restTemplate.getForEntity("https://api.github.com/users/" +
                    ESAPI.encoder().encodeForURL(userName), GitUser.class);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(ErrorMessage.builder().errorMessage(e.getMessage()).build(), e.getStatusCode());
        }
        Optional<GitUser> user = Optional.ofNullable(response.getBody());
        if (user.isPresent()) {
            GitUser gitUser = user.get();
            collectGitUserService.storeCountedQueryAmount(gitUser);
            gitUser.setCalculations(calculationService.calculateWeight(gitUser));
            return new ResponseEntity<>(GitUserDTO.buildFromGitUser(gitUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ErrorMessage.builder().errorMessage("Not found").build(), HttpStatus.NOT_FOUND);
        }
    }
}
