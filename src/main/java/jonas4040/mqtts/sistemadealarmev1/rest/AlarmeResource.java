package jonas4040.mqtts.sistemadealarmev1.rest;

import jonas4040.mqtts.sistemadealarmev1.service.AlarmeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alarme")
public class AlarmeResource {
    @Autowired
    private AlarmeService alarmeService;

    @GetMapping("/temperatura")
    public String getTemperatura(){
       return alarmeService.subscrever("camara/", 1);
    }

    //TODO mudo ou nao para POST?
    @GetMapping("/reset")
    public void resetar(){
        alarmeService.resetarAlarme();
    }
}
