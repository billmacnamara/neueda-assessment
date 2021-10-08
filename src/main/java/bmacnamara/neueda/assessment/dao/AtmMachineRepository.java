package bmacnamara.neueda.assessment.dao;

import bmacnamara.neueda.assessment.model.AtmMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtmMachineRepository extends JpaRepository<AtmMachine, Integer> {

    @Override
    <S extends AtmMachine>S save(S entity);

    AtmMachine findAtmMachineById(int id);

}
