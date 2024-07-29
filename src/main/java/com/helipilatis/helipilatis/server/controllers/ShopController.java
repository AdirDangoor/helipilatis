package com.helipilatis.helipilatis.server.controllers;
import com.helipilatis.helipilatis.server.requests.Ticket1Request;
import com.helipilatis.helipilatis.client.ShopView;
import com.helipilatis.helipilatis.server.requests.LoginRequest;
import com.helipilatis.helipilatis.server.requests.RegisterRequest;
import com.helipilatis.helipilatis.server.services.AuthService;
import com.helipilatis.helipilatis.server.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.logging.Logger;


@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopService shopService;
    private final Logger logger = Logger.getLogger(ShopController.class.getName());

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping
    public ResponseEntity<String> purchaseTicket(@RequestBody Ticket1Request ticket1Request) {
        try {
            // Call the service to process the ticket purchase
            shopService.purchaseTicket(ticket1Request);
            logger.info("Ticket purchased successfully. Ticket count: " + ticket1Request.getTicketNum());
            return ResponseEntity.ok("Ticket purchased successfully");
        } catch (Exception e) {
            logger.warning("Error purchasing ticket: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error purchasing ticket: " + e.getMessage());
        }
    }
}



//get a request from front to purchase ticket


//ask shop service for 1 ticket for client x

//if get ticket then send the client 1 ticket
//@RestController
//@RequestMapping("/api/auth") //request mapping
//public class AuthController {
//
//    @Autowired
//    private AuthService authService;
//    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
//
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
//        // print for starting the function :
//        logger.info("loginRequest : " + loginRequest);
//        return ResponseEntity.ok("Login successful");
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
//
//        // print for starting the function :
//        logger.info("registerRequest : " + registerRequest);
//
//        boolean isRegistered = authService.register(registerRequest);
//        if (isRegistered) {
//            return ResponseEntity.ok("Register successful");
//        } else {
//            return ResponseEntity.status(400).body("Register failed");
//        }
//    }

