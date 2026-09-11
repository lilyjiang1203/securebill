package dmit2015.repository;

import dmit2015.entity.Bill;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import net.datafaker.Faker;

import java.util.Random;
import java.util.logging.Logger;

@ApplicationScoped
public class BillInitializer {
    private final Logger _logger = Logger.getLogger(BillInitializer.class.getName());

    @Inject
    private BillRepository _billRepository;

    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {
        _logger.info("Initializing bills");

        if (_billRepository.count() == 0) {

            try {
                // Generate 10 fake Bills
                var faker = new Faker();
                var random = new Random();

                String[] sampleUsernames = {
                        "student1@nait.ca",
                        "aabalos",
                        "student3@nait.ca"
                };

                for (int count = 1; count <= 10; count++) {
                    var currentBill = Bill.of(faker);
                    String username = sampleUsernames[random.nextInt(sampleUsernames.length)];
                    currentBill.setUsername(username);
                    _billRepository.add(currentBill);
                }

            } catch (Exception ex) {
                _logger.fine(ex.getMessage());
            }

            _logger.info("Created " + _billRepository.count() + " records.");
        }
    }
}