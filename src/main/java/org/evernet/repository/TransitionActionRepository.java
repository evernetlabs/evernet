package org.evernet.repository;

import org.evernet.model.TransitionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransitionActionRepository extends JpaRepository<TransitionAction, String> {

}
