package io.ysalih.mulberryCS.repository;

import io.ysalih.mulberryCS.model.Realestate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealestateRepository extends JpaRepository<Realestate, Long> {}
