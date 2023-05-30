package com.npt247.backend.repositories;

import com.npt247.backend.models.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaintingRepository extends JpaRepository<Painting,Long> {
}
