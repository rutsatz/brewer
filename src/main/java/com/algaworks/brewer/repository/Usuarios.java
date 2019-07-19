package com.algaworks.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.repository.helper.usuario.UsuariosQueries;

public interface Usuarios extends JpaRepository<Usuario, Long>, UsuariosQueries {

    public Optional<Usuario> findByEmail(String email);

    /* Exemplo de consulta usando o @Query do JPA direto na interface. */
    @Query(value = "select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario")
    public List<String> permissoesComQuery(@Param("usuario") Usuario usuario);



}