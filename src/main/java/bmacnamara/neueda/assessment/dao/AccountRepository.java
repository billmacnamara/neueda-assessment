package bmacnamara.neueda.assessment.dao;

import bmacnamara.neueda.assessment.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Override
    <S extends Account>S save(S entity);

    Account findAccountByAccountNumber(int accountNumber);

    @Modifying
    @Transactional
    @Query(value = "update Account a set a.balance = :newBalance where a.accountNumber = :accountNumber")
    void setNewBalance(@Param("accountNumber") int accountNumber, @Param("newBalance") double newBalance);

}
