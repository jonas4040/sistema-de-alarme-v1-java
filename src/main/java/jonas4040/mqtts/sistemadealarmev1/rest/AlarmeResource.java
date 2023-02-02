package jonas4040.mqtts.sistemadealarmev1.rest;

import jonas4040.mqtts.sistemadealarmev1.service.AlarmeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/alarme")
public class AlarmeResource {
    @Autowired
    private AlarmeService alarmeService;

    @GetMapping("/temperatura")
    public ResponseEntity<String> getTemperatura(){
        return new ResponseEntity<>(alarmeService.  subscrever("casa/temperatura/quarto1", 1), HttpStatus.OK) ;
    }

    @GetMapping("/reset")
    public ResponseEntity<String> resetar(){
        alarmeService.resetarAlarme();
        return new ResponseEntity ("{\"situacao\":\"Alarme resetado com sucesso!\"}",HttpStatus.OK);
    }
}
