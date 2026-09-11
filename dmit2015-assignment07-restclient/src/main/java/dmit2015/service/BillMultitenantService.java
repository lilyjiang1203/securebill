package dmit2015.service;

import dmit2015.model.BillMultitenant;

import java.util.List;
import java.util.Optional;

public interface BillMultitenantService {

    BillMultitenant createBillMultitenant(BillMultitenant billMultitenant);

    Optional<BillMultitenant> getBillMultitenantById(Long id);

    List<BillMultitenant> getAllBillMultitenants();

    BillMultitenant updateBillMultitenant(BillMultitenant billMultitenant);

    void deleteBillMultitenantById(Long id);
}