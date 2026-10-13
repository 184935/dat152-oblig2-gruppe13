/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

    @Autowired
    private OrderService orderService;

	// TODO - getAllBorrowOrders (@Mappings, URI=/orders, and method) + filter by expiry and paginate 
	@GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllBorrowOrders(@RequestParam(required = false) LocalDate expiry, Pageable page) {

        List<Order> orders;
        if (expiry == null || page == null) {
            orders = orderService.findAllOrders();
        } else {
            orders = orderService.findByExpiryDate(expiry, page);
        }
        for (Order o : orders) {
            o.add(linkTo(methodOn(OrderController.class).getBorrowOrder(o.getId())).withSelfRel());
            o.add(linkTo(methodOn(OrderController.class).deleteBookOrder(o.getId())).withSelfRel());
            o.add(linkTo(methodOn(OrderController.class).updateOrder(o.getId(), o)).withSelfRel());
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
	// TODO - getBorrowOrder (@Mappings, URI=/orders/{id}, and method)
	@GetMapping("/orders/{id}")
    public ResponseEntity<Order> getBorrowOrder(@PathVariable Long id)  {
        try {
            Order order = orderService.findOrder(id);
            order.add(linkTo(methodOn(OrderController.class).getBorrowOrder(order.getId())).withSelfRel());
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (OrderNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	// TODO - updateOrder (@Mappings, URI=/orders/{id}, and method)
	@PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        try {
            Order order1 = orderService.updateOrder(order, id);
            return new ResponseEntity<>(order1, HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
	// TODO - deleteBookOrder (@Mappings, URI=/orders/{id}, and method)
	@DeleteMapping("/orders/{id}")
    public ResponseEntity<Order> deleteBookOrder(@PathVariable Long id)  {

        try {
            orderService.deleteOrder(id);
            return new ResponseEntity<>(HttpStatus.OK );
        } catch (OrderNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }
}
