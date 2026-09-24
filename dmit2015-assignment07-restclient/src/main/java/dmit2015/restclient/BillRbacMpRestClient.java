package dmit2015.restclient;

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
@RegisterRestClient(configKey = "rbac-api")
@Path("BillDtos")
public interface BillRbacMpRestClient {

    @POST
    Response create(  @HeaderParam("Authorization") String authorizationHeader,
                      BillRbac newBill);

    @GET
    List<BillRbac> findAll(@HeaderParam("Authorization") String authorizationHeader);

    @GET
    @Path("/{id}")
    Optional<BillRbac> findById(@HeaderParam("Authorization") String authorizationHeader, @PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    BillRbac update(@HeaderParam("Authorization") String authorizationHeader,@PathParam("id") Long id, BillRbac updatedBill);

    @DELETE
    @Path("/{id}")
    void delete(@HeaderParam("Authorization") String authorizationHeader,@PathParam("id") Long id);

}