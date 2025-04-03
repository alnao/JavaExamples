package it.alnao.corso25saluti.controller;

import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/corso25saluti")    //http://localhost:5432/api/v1/corso25saluti
public class Corso25salutiController {

    @GetMapping()
    public String getSaluto(){
        return "Ciao Corso 25 Saluti!";
    }

    @GetMapping(value = "/{nome}")
    public String getSalutoConNome(@PathVariable(value = "nome") String nome) {
        return "Ciao " + nome + "!";
    }
}
