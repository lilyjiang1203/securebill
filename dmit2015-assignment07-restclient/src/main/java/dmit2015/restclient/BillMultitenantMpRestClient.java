package dmit2015.restclient;

import dmit2015.model.BillMultitenant;
import dmit2015.model.BillRbac;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Optional;

@RequestScoped
@RegisterProvider(BadRequestResponseMapper.class)
@RegisterRestClient(baseUri = "http://localhost:8182/restapi/BillDtos")
public interface BillMultitenantMpRestClient {

    @POST
    Response create( @HeaderParam("Authorization") String authorization,
                     BillMultitenant billMultitenant);

    @GET
    List<BillMultitenant> findAll(@HeaderParam("Authorization") String authorizationHeader);

    @GET
    @Path("/{id}")
    Optional<BillMultitenant> findById(@HeaderParam("Authorization") String authorizationHeader,@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    BillMultitenant update(@HeaderParam("Authorization") String authorizationHeader,@PathParam("id") Long id, BillMultitenant updatedBill);

    @DELETE
    @Path("/{id}")
    void delete(@HeaderParam("Authorization") String authorizationHeader,@PathParam("id") Long id);

}