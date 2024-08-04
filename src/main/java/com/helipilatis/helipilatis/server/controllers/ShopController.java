package com.helipilatis.helipilatis.server.controllers;
import com.helipilatis.helipilatis.databaseModels.TicketType;
import com.helipilatis.helipilatis.server.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    /**
     * Handles GET requests to retrieve all ticket types.
     * @return a ResponseEntity containing a list of TicketType objects and an HTTP status code
     */
    @GetMapping("/ticketTypes")
    public ResponseEntity<List<TicketType>> getAllTicketTypes() {
        try {
            List<TicketType> ticketTypes = shopService.getAllTicketTypes();
            return ResponseEntity.ok(ticketTypes);
        } catch (Exception e) {
            logger.warning("Error getting ticket types: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Handles POST requests to purchase a ticket.
     * @param userId the ID of the user purchasing the ticket
     * @param ticketTypeId the ID of the ticket type being purchased
     * @return a ResponseEntity containing a success message and an HTTP status code
     */
    @PostMapping("/purchaseTicket")
    public ResponseEntity<String> purchaseTicket(@RequestParam Long userId, @RequestParam Long ticketTypeId) {
        try {
            shopService.purchaseTicket(userId, ticketTypeId);
            logger.info("Ticket purchased successfully. User ID: " + userId + ", Ticket Type ID: " + ticketTypeId);
            return ResponseEntity.ok("Ticket purchased successfully");
        } catch (Exception e) {
            logger.warning("Error purchasing ticket: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error purchasing ticket: " + e.getMessage());
        }
    }
}

