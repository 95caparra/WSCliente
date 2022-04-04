package com.cmc.clientes.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmc.clientes.entity.Cliente;


public interface IClienteDAO extends JpaRepository<Cliente, Long>{

	

}
