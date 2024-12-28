package io.ysalih.mulberryCS.repository;

import io.ysalih.mulberryCS.model.Realestate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealestateRepository extends JpaRepository<Realestate, Long> {

    @Query("""
            SELECT r FROM Realestate r
            WHERE (:price IS NULL OR r.price < :price)
              AND (:type IS NULL OR r.type = :type)
              AND (:city IS NULL OR r.city = :city)
              AND (:status IS NULL OR r.status = :status)
           """)
    List<Realestate> findByFilter(@Param("price") Integer price,
                                  @Param("type") String type,
                                  @Param("city") String city,
                                  @Param("status") String status);
}
