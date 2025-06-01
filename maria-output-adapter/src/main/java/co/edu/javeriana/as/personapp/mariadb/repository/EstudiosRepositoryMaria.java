package co.edu.javeriana.as.personapp.mariadb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;

public interface EstudiosRepositoryMaria extends JpaRepository<EstudiosEntity, EstudiosEntityPK> {
	
	@Query("SELECT e FROM EstudiosEntity e WHERE e.estudiosEntityPK.ccPer = :ccPer")
	List<EstudiosEntity> findByCcPer(@Param("ccPer") Integer ccPer);
	
	@Query("SELECT e FROM EstudiosEntity e WHERE e.estudiosEntityPK.idProf = :idProf")
	List<EstudiosEntity> findByIdProf(@Param("idProf") Integer idProf);
}