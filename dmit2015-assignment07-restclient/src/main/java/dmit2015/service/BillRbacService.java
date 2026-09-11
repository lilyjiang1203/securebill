package dmit2015.service;

import dmit2015.model.BillRbac;

import java.util.List;
import java.util.Optional;

public interface BillRbacService {

    BillRbac createBillDto(BillRbac billRbac);

    Optional<BillRbac> getBillDtoById(Long id);

    List<BillRbac> getAllBillDtos();

    BillRbac updateBillDto(BillRbac billRbac);

    void deleteBillDtoById(Long id);
}