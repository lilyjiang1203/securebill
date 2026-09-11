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

import java.net.URI;

/**
 * This Jakarta RESTful Web Services root resource class provides common REST API endpoints to
 * perform CRUD operations on Jakarta Persistence entity.
 */
@ApplicationScoped
@Path("Bills")                    // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)    // All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)    // All methods returns data that has been converted to JSON format
public class BillResource {

    @Inject
    private BillRepository _billRepository;

    @GET    // This method only accepts HTTP GET requests.
    public Response listBills() {
        return Response.ok(_billRepository.findAll()).build();
    }

    @Path("{id}")
    @GET    // This method only accepts HTTP GET requests.
    public Response findBillById(@PathParam("id") Long id) {
        Bill existingBill = _billRepository.findById(id).orElseThrow(NotFoundException::new);

        return Response.ok(existingBill).build();
    }

    @POST    // This method only accepts HTTP POST requests.
    public Response addBill(Bill newBill, @Context UriInfo uriInfo) {

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
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // userInfo is injected via @Context parameter to this method
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newBill.getId()))
                .build();

        // Set the location path of the new entity with its identifier
        // Returns an HTTP status of "201 Created" if the Bill was successfully persisted
        return Response
                .created(location)
                .build();
    }

    @PUT            // This method only accepts HTTP PUT requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response updateBill(@PathParam("id") Long id, Bill updatedBill) {
        if (!id.equals(updatedBill.getId())) {
            throw new BadRequestException();
        }

        String errorMessage = JavaBeanValidator.validateBean(updatedBill);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        Bill existingBill = _billRepository
                .findById(id)
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
            // Return an HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "200 OK" and include in the body of the response the object that was updated
        return Response.ok(existingBill).build();
    }

    @DELETE            // This method only accepts HTTP DELETE requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response delete(@PathParam("id") Long id) {

        Bill existingBill = _billRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        try {
            _billRepository.delete(existingBill);    // Removes the Bill from being persisted
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response
                    .serverError()
                    .encoding(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "204 No Content" to indicated that the resource was deleted
        return Response.noContent().build();
    }

}