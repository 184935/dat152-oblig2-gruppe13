/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class UserController {

	// TODO authority annotation
    @Autowired
    private UserService userService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Object> getUsers(){

        List<User> users = userService.findAllUsers();

        if(users.isEmpty())

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) throws UserNotFoundException, OrderNotFoundException{

        User user = userService.findUser(id);

        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    // TODO - createUser (@Mappings, URI=/users, and method)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }
    // TODO - updateUser (@Mappings, URI, and method)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.updateUser(user, id), HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // TODO - deleteUser (@Mappings, URI, and method)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // TODO - getUserOrders (@Mappings, URI=/users/{id}/orders, and method)
    @GetMapping("/users/{id}/orders")
    public ResponseEntity<Set<Order>> getUserOrders(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(userService.getUserOrders(id), HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
    // TODO - getUserOrder (@Mappings, URI=/users/{uid}/orders/{oid}, and method)
    @GetMapping("/users/{uid}/orders/{oid}")
    public ResponseEntity<Order> getUserOrder(@PathVariable Long uid, @PathVariable Long oid) {
        try {
            return new ResponseEntity<>(userService.getUserOrder(uid, oid), HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
    // TODO - deleteUserOrder (@Mappings, URI, and method)
    @DeleteMapping("/users/{uid}/orders/{oid}")
    public ResponseEntity<Order> deleteUserOrder(@PathVariable Long uid, @PathVariable Long oid) {
        try {
            userService.deleteOrderForUser(uid, oid);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // TODO - createUserOrder (@Mappings, URI, and method) + HATEOAS links
    @PostMapping("users/{id}/orders")
    public ResponseEntity<Set<Order>> createUserOrder(@PathVariable Long id, @RequestBody Order order) {
        try {
//			order.add(linkTo(methodOn(OrderController.class).getBorrowOrder(order.getId())).withSelfRel());
//			order.add(linkTo(methodOn(OrderController.class).deleteBookOrder(order.getId())).withSelfRel());
//			order.add(linkTo(methodOn(OrderController.class).updateOrder(order.getId(), order)).withSelfRel());
            User user = userService.createOrdersForUser(id, order);
            for (Order o : user.getOrders()) {
                o.add(linkTo(methodOn(OrderController.class).getBorrowOrder(o.getId())).withSelfRel());
                o.add(linkTo(methodOn(OrderController.class).deleteBookOrder(o.getId())).withSelfRel());
                o.add(linkTo(methodOn(OrderController.class).updateOrder(o.getId(), o)).withSelfRel());
            }
            return new ResponseEntity<>(user.getOrders(), HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
	
}
