package dmit2015.service;

import dmit2015.faces.LoginSession;
import dmit2015.model.BillMultitenant;
import dmit2015.restclient.BillMultitenantMpRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@Named("currentMpRestClientBillMultitenantService")
@ApplicationScoped
public class MpRestClientBillMultitenantService implements BillMultitenantService {

    @Inject
    private LoginSession _loginSession;

    @Inject
    @RestClient
    private BillMultitenantMpRestClient mpRestClient;

    @Override
    public BillMultitenant createBillMultitenant(BillMultitenant billMultitenant) {
        String authorizationHeader = _loginSession.getAuthorization();
        try(Response response = mpRestClient.create(authorizationHeader,billMultitenant)) {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String location = response.getHeaderString("Location");
                int resourceIdIndex = location.lastIndexOf("/") + 1;
                Long resourceId = Long.parseLong(location.substring(resourceIdIndex));
                billMultitenant.setId(resourceId);
            }
        }
        return billMultitenant;
    }

    @Override
    public Optional<BillMultitenant> getBillMultitenantById(Long id) {
        String authorizationHeader = _loginSession.getAuthorization();
        return mpRestClient.findById(authorizationHeader, id);
    }


    @Override
    public List<BillMultitenant> getAllBillMultitenants() {
        String authorizationHeader = _loginSession.getAuthorization();
        return mpRestClient.findAll(authorizationHeader);
    }

    @Override
    public BillMultitenant updateBillMultitenant(BillMultitenant billMultitenant) {
        String authorizationHeader = _loginSession.getAuthorization();
        return mpRestClient.update(authorizationHeader,billMultitenant.getId(), billMultitenant);
    }

    @Override
    public void deleteBillMultitenantById(Long id) {
        String authorizationHeader = _loginSession.getAuthorization();
        mpRestClient.delete(authorizationHeader,id);
    }
}
