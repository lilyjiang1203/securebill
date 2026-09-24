package dmit2015.service;

import dmit2015.faces.LoginSession;
import dmit2015.model.BillRbac;
import dmit2015.restclient.BillRbacMpRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@Named("currentMpRestClientBillRbacService")
@ApplicationScoped
public class MpRestClientBillRbacService implements BillRbacService {

    @Inject
    private LoginSession _loginSession;

    @Inject
    @RestClient
    private BillRbacMpRestClient restClient;

    @Override
    public BillRbac createBillDto(BillRbac billRbac) {
        String authorizationHeader = _loginSession.getAuthorization();
        try (Response response = restClient.create(authorizationHeader, billRbac)) {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String location = response.getHeaderString("Location");
                int resourceIdIndex = location.lastIndexOf("/") + 1;
                Long resourceId = Long.parseLong(location.substring(resourceIdIndex));
                billRbac.setId(resourceId);
            }
        }
        return billRbac;
    }

    @Override
    public Optional<BillRbac> getBillDtoById(Long id) {
        String authorizationHeader = _loginSession.getAuthorization();
        return restClient.findById(authorizationHeader,id);
    }

    @Override
    public List<BillRbac> getAllBillDtos() {
        String authorizationHeader = _loginSession.getAuthorization();

        System.out.println("DEBUG: getAllBillDtos() called");
        System.out.println("DEBUG: Authorization present = "
                + (authorizationHeader != null && !authorizationHeader.isBlank()));

        List<BillRbac> bills = restClient.findAll(authorizationHeader);

        System.out.println("DEBUG: RBAC returned "
                + (bills == null ? "null" : bills.size() + " bills"));

        return bills;
    }

    @Override
    public BillRbac updateBillDto(BillRbac billRbac) {
        String authorizationHeader = _loginSession.getAuthorization();
        return restClient.update(authorizationHeader,billRbac.getId(), billRbac);
    }

    @Override
    public void deleteBillDtoById(Long id) {
        String authorizationHeader = _loginSession.getAuthorization();
        restClient.delete(authorizationHeader,id);
    }
}
