package dmit2015.model;

import jakarta.json.bind.annotation.JsonbDateFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BillRbac {

    private Long id;

    private String payeeName;

    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate dueDate;

    private BigDecimal paymentDue;

    private boolean paid;

    private Integer version;

}
