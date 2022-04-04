package com.cmc.clientes.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmc.clientes.dto.ClienteDTO;
import com.cmc.clientes.entity.Cliente;
import com.cmc.clientes.services.IClienteService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
    private ModelMapper modelMapper;
	
	@Autowired
	private IClienteService clienteService;

	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();
	}

	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		Cliente objeto = null;

		try {
			objeto = clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (objeto == null) {
			response.put("mensaje",
					"El Cliente con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		ClienteDTO clienteDTO = modelMapper.map(objeto, ClienteDTO.class);
		response.put("objeto", clienteDTO);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}		

	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody ClienteDTO objeto, BindingResult result) {

		Cliente objetoNew = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {

			List<String> errors = result.getFieldErrors().stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());

			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			
			Cliente cliente = modelMapper.map(objeto, Cliente.class);
			
			if(cliente.getPrimerNombre().length() > 10 && cliente.getPrimerNombre().length() < 50) {
				if(cliente.getPrimerApellido().length() > 10 && cliente.getPrimerApellido().length() < 100) {
					objetoNew = clienteService.save(cliente);
					response.put("mensaje", "El objeto ha sido creado con éxito!");
					response.put("objeto", objetoNew);
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
				} else {
					response.put("mensaje", "Error al realizar el insert en la base de datos, primer apellido no "
							+ "cumple, debe ser > 10 y < de 100 caracteres ");
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
				}				
			} else {
				response.put("mensaje", "Error al realizar el insert en la base de datos, primer nombre no "
						+ "cumple, debe ser > 10 y < de 50 caracteres ");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			}
				
			

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody ClienteDTO objeto, BindingResult result, @PathVariable Long id) {

		Cliente objetoActual = clienteService.findById(id);

		Cliente objetoUpdated = null;

		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {

			List<String> errors = result.getFieldErrors().stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());

			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		if (objetoActual == null) {
			response.put("mensaje", "Error: no se pudo editar, el  ID: "
					.concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {

			Cliente cliente = modelMapper.map(objeto, Cliente.class);
			cliente.setId(id);

			objetoUpdated = clienteService.save(cliente);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el objeto en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El objeto ha sido actualizado con éxito!");
		response.put("objeto", objetoUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/cliente/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {

			clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el objeto de la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El objeto eliminado con éxito!");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
}
