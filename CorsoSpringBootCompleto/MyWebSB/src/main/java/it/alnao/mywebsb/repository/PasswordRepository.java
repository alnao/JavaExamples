package it.alnao.mywebsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.alnao.mywebsb.entities.Password;

public interface PasswordRepository extends JpaRepository<Password,String>{

}
