package org.evernet.repository;

import org.evernet.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

    Admin findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);
}
