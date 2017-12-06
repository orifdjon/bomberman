package bomber.gameservice.controller;


import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.concurrent.atomic.AtomicInteger;


@Controller
@RequestMapping("/game")
public class GameController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GameController.class);
    private static AtomicInteger connectedPlayerCount = new AtomicInteger(0);

    /**
     * curl -i localhost:8090/game/create
     */
    @RequestMapping(
            path = "/checkstatus",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok().body(new Integer(connectedPlayerCount.intValue()).toString());//возращает gameId
    }

    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> create(@RequestParam("playerCount") String playerCount) {
        log.info("Game has been created playerCount={}", playerCount);
        return ResponseEntity.ok().body("42");//возращает gameId
    }

    @RequestMapping(
            path = "/start",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> start(@RequestParam("gameId") String gameId) {
        log.info("Game has been started gameId={}", gameId);
        return ResponseEntity.ok().body("42"); //возращает gameId
    }

    public static int getConnectedPlayerCount() {
        return connectedPlayerCount.intValue();
    }

    public static synchronized void setConnectedPlayerCount(int value) {
        connectedPlayerCount.set(value);
    }
}
