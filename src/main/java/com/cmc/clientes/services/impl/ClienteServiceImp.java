package com.cmc.clientes.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmc.clientes.dao.IClienteDAO;
import com.cmc.clientes.entity.Cliente;
import com.cmc.clientes.services.IClienteService;

@Service
public class ClienteServiceImp implements IClienteService {
	
	@Autowired
	private IClienteDAO clienteDAO;
	
	@Override
	public List<Cliente> findAll() {
		return clienteDAO.findAll();
	}

	@Override
	public Page<Cliente> findAll(Pageable pageable) {
		return clienteDAO.findAll(pageable);
	}

	@Override
	public Cliente findById(Long id) {
		return clienteDAO.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Cliente save(Cliente Cliente) {
		return clienteDAO.save(Cliente);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clienteDAO.deleteById(id);
	}
	
}
