package dmit2015.resource;


import common.validation.JavaBeanValidator;
import jakarta.annotation.security.RolesAllowed;
import dmit2015.dto.BillDto;
import dmit2015.entity.Bill;
import dmit2015.mapper.BillMapper;
import dmit2015.repository.BillRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.Claims;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This Jakarta RESTful Web Services root resource class provides common REST API endpoints to
 * perform CRUD operations on the DTO (Data Transfer Object) for a Jakarta Persistence entity.
 */
@ApplicationScoped
@Path("BillDtos")                // All methods in this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)
// All methods in this class expects method parameters to contain data in JSON format
@Produces(MediaType.APPLICATION_JSON)    // All methods in this class returns data in JSON format
public class BillDtoResource {
    @Inject
    @Claim(standard = Claims.upn)   // The username for the user.
    private ClaimValue<Optional<String>> optionalUsername;

    @Inject
    @Claim(standard = Claims.groups)    // The roles that the subject is a member of.
    private ClaimValue<Optional<Set<String>>> optionalGroups;

    @Inject
    private BillRepository _billRepository;

    @GET
    @RolesAllowed({"ActiveStudent", "Accounting", "Executive"})
    public Response findAllBills() {
        return Response.ok(
                _billRepository
                        .findAll()
                        .stream()
                        .map(BillMapper.INSTANCE::toDto)
                        .collect(Collectors.toList())
        ).build();
    }

    @RolesAllowed({"ActiveStudent", "Accounting", "Executive"})
    @Path("{id}")
    @GET    // This method only accepts HTTP GET requests.
    public Response findBillById(@PathParam("id") Long id) {
        Bill existingBill = _billRepository.findById(id).orElseThrow(NotFoundException::new);

        BillDto dto = BillMapper.INSTANCE.toDto(existingBill);

        return Response.ok(dto).build();
    }

    @POST
    @RolesAllowed({"ActiveStudent", "Accounting"})// This method only accepts HTTP POST requests.
    public Response createBill(BillDto dto, @Context UriInfo uriInfo) {
        Bill newBill = BillMapper.INSTANCE.toEntity(dto);

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

        // uriInfo is injected via @Context parameter to this method
        URI location = UriBuilder
                .fromPath(uriInfo.getPath())
                .path("{id}")
                .build(newBill.getId());

        // Set the location path of the new entity with its identifier
        // Returns an HTTP status of "201 Created" if the Bill was created.
        return Response
                .created(location)
                .build();
    }

    @PUT            // This method only accepts HTTP PUT requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    @RolesAllowed("ActiveStudent")
    public Response updateBill(@PathParam("id") Long id, BillDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BadRequestException();
        }

        Bill existingBill = _billRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        Bill updatedBill = BillMapper.INSTANCE.toEntity(dto);

        String errorMessage = JavaBeanValidator.validateBean(updatedBill);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

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
        BillDto updatedDto = BillMapper.INSTANCE.toDto(existingBill);
        return Response.ok(updatedDto).build();
    }

    @DELETE            // This method only accepts HTTP DELETE requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    @RolesAllowed("Executive")
    public Response deleteBill(@PathParam("id") Long id) {

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

        // Returns an HTTP status "204 No Content" to indicate the resource was deleted
        return Response.noContent().build();

    }

}