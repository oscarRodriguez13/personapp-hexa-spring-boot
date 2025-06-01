package co.edu.javeriana.as.personapp.mariadb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;

public interface TelefonoRepositoryMaria extends JpaRepository<TelefonoEntity, String> {
	
	@Query("SELECT t FROM TelefonoEntity t WHERE t.duenio.cc = :ownerCc")
	List<TelefonoEntity> findByDuenioCc(@Param("ownerCc") Integer ownerCc);
}