package at.ac.tuwien.inso.sqm.repository;

import org.springframework.data.repository.CrudRepository;

import at.ac.tuwien.inso.sqm.entity.PendingAcountActivation;

public interface PendingAccountActivationRepository extends CrudRepository<PendingAcountActivation, String> {
}
