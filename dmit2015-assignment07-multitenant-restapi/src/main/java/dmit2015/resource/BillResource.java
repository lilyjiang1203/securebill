package dmit2015.resource;

import common.validation.JavaBeanValidator;
import dmit2015.entity.Bill;
import dmit2015.repository.BillRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.Claims;

import java.net.URI;
import java.util.Optional;
import java.util.Set;


/**
 * This Jakarta Persistence RESTful Web Services root resource class provides common REST API endpoints to
 * perform CRUD operations on Jakarta Persistence entity.
 */
@ApplicationScoped
@Path("Bills")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BillResource {

    @Inject
    private BillRepository _billRepository;


    @Inject
    @Claim("preferred_username")
    private ClaimValue<Optional<String>> optionalUsername;


    @Inject
    @Claim(standard = Claims.groups)
    private ClaimValue<Optional<Set<String>>> optionalGroups;


    private String currentUsername() {
        return optionalUsername.getValue()
                .orElseThrow(() -> new NotAuthorizedException("User identity is required."));
    }


    @GET
    public Response listBills() {

        String username = currentUsername();

        Set<String> groups = optionalGroups.getValue()
                .orElseGet(Set::of);

        // Accounting and Executive can see all Bills
        if (groups.contains("Accounting") || groups.contains("Executive")) {
            return Response.ok(
                    _billRepository.findAll()
            ).build();
        }

        // ActiveStudent can only see their own Bills
        return Response.ok(
                _billRepository.findByUsername(username)
        ).build();
    }


    @Path("{id}")
    @GET
    public Response findBillById(@PathParam("id") Long id) {

        String username = currentUsername();

        Set<String> groups = optionalGroups.getValue()
                .orElseGet(Set::of);

        Bill existingBill;

        // Accounting and Executive can view any Bill
        if (groups.contains("Accounting") || groups.contains("Executive")) {

            existingBill = _billRepository
                    .findById(id)
                    .orElseThrow(NotFoundException::new);

        } else {

            // ActiveStudent can only view their own Bill
            existingBill = _billRepository
                    .findByIdAndUsername(id, username)
                    .orElseThrow(NotFoundException::new);
        }

        return Response.ok(existingBill).build();
    }


    @POST
    public Response addBill(Bill newBill, @Context UriInfo uriInfo) {

        newBill.setUsername(currentUsername());

        String errorMessage = JavaBeanValidator.validateBean(newBill);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        try {
            // Persist the new Bill into the database
            _billRepository.add(newBill);
        } catch (Exception ex) {
            return Response
                    .serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newBill.getId()))
                .build();

        return Response
                .created(location)
                .build();
    }


    @PUT
    @Path("{id}")
    public Response updateBill(@PathParam("id") Long id, Bill updatedBill) {

        if (!id.equals(updatedBill.getId())) {
            throw new BadRequestException();
        }

        updatedBill.setUsername(currentUsername());

        String errorMessage = JavaBeanValidator.validateBean(updatedBill);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        Bill existingBill = _billRepository
                .findByIdAndUsername(id, currentUsername())
                .orElseThrow(NotFoundException::new);

        existingBill.setVersion(updatedBill.getVersion());
        existingBill.setPayeeName(updatedBill.getPayeeName());
        existingBill.setPaymentDue(updatedBill.getPaymentDue());
        existingBill.setDueDate(updatedBill.getDueDate());
        existingBill.setPaid(updatedBill.isPaid());

        try {
            _billRepository.update(existingBill);
        } catch (OptimisticLockException ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("The data you are trying to update has changed since your last read request.")
                    .build();
        } catch (Exception ex) {
            return Response
                    .serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        return Response.ok(existingBill).build();
    }


    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {

        Bill existingBill = _billRepository
                .findByIdAndUsername(id, currentUsername())
                .orElseThrow(NotFoundException::new);

        try {
            _billRepository.delete(existingBill);
        } catch (Exception ex) {
            return Response
                    .serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        return Response.noContent().build();
    }
}