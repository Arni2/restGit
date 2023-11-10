package org.arni.jpa;

import org.arni.model.LoginCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginCountRepository extends JpaRepository<LoginCount, Long> {
}
